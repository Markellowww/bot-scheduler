package io.tgbot.moaishelper.service;

import com.vdurmont.emoji.EmojiParser;
import io.tgbot.moaishelper.config.BotConfig;
import io.tgbot.moaishelper.keyboard.KeyboardMarkupProvider;
import io.tgbot.moaishelper.text.KeyboardText;
import io.tgbot.moaishelper.model.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.tgbot.moaishelper.text.Info.*;

/**
 * @Authors: Markelloww & YDK
 */

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final StatusRepository statusRepository;
    private final GroupHandler groupHandler;
    private final MessageHandler messageHandler;
    private final BotConfig config;

    public TelegramBot(BotConfig config,
                       @Lazy GroupHandler groupHandler,
                       @Lazy MessageHandler messageHandler,
                       StatusRepository statusRepository,
                       GroupRepository groupRepository,
                       UserRepository userRepository) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        CommandInitializer.init(listOfCommands);
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        }
        catch (TelegramApiException _) {}
        this.groupHandler = groupHandler;
        this.messageHandler = messageHandler;
        this.statusRepository = statusRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String getBotUsername() {
        return config.getName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            User user = userRepository.findByChatId(chatId);
            String messageText = update.getMessage().getText();

            if (messageText.equals("/start") || user == null) {
                registerUser(update.getMessage());
                messageHandler.startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                return;
            }

            if (user.getStatus().getId() != 1) {
                switch (user.getStatus().getId()) {
                    case 2: {
                        groupHandler.handleGroupNameInput(chatId, messageText);
                        break;
                    }
                    case 3: {
                        messageText = messageHandler.checkForAtInMessage(messageText);
                        groupHandler.handleInviteUserInput(chatId, messageText);
                        break;
                    }
                    case 5: {
                        messageText = messageHandler.checkForAtInMessage(messageText);
                        groupHandler.handleKickUserInput(chatId, messageText);
                        break;
                    }
                    case 6: {
                        messageText = messageHandler.checkForAtInMessage(messageText);
                        groupHandler.handleGiveOwnerInput(chatId, messageText);
                        break;
                    }
                    case 7: {
                        messageText = messageHandler.checkForAtInMessage(messageText);
                        groupHandler.handleSetAdminInput(chatId, messageText);
                        break;
                    }
                    case 8: {
                        messageText = messageHandler.checkForAtInMessage(messageText);
                        groupHandler.handleRemoveAdminInput(chatId, messageText);
                        break;
                    }
                    default: {
                        messageHandler.sendMessageWithKeyboardMarkup(chatId, OPERATION_CANCELLED,
                                KeyboardMarkupProvider.inlineContinueButtonToMainMenu());
                        user.setStatus(statusRepository.findById(1));
                        userRepository.save(user);
                        break;
                    }
                }
            } 
            else {
                if (messageText.equals(KeyboardText.NOTIFICATION_SETTING)) {
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, "Ваши настройки уведомлений:",
                            KeyboardMarkupProvider.notificationMenu());
                }
                else if (messageText.equals(KeyboardText.GO_TO_GROUPS)) {
                    boolean chosen = groupHandler.groupChosen(user), created = groupHandler.groupCreated(user);
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, GROUP_MENU(user),
                            KeyboardMarkupProvider.groupsMenu(chosen, created));
                }
                else if (messageText.equals(KeyboardText.CONTACTS)) {
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, TEXT_SUPPORT,
                            KeyboardMarkupProvider.inlineGoBackButton());
                }
                else if (messageText.equals(KeyboardText.GUIDE)) {
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, TEXT_IN_DEVELOP,
                            KeyboardMarkupProvider.inlineGoBackButton());
                }
                else if (messageText.equals(KeyboardText.BACK_TO_GROUPS)) {
                    boolean chosen = groupHandler.groupChosen(user), created = groupHandler.groupCreated(user);
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, GROUP_MENU(user),
                            KeyboardMarkupProvider.groupsMenu(chosen, created));
                }
                else if (messageText.equals(KeyboardText.LEAVE_GROUP)) {
                    groupHandler.leaveGroup(chatId, userRepository.findByChatId(chatId).getSelectedGroup());
                }
                else if (messageText.equals(KeyboardText.GROUP_HANDLER)) {
                    boolean isOwner = user.getId() == user.getSelectedGroup().getOwner().getId();
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, "Управление группой",
                                KeyboardMarkupProvider.groupSettingsMenu(isOwner));
                }
                else if (messageText.equals(KeyboardText.BACK_TO_MENU_GROUPS)) {
                    if (user.getSelectedGroup() == null) {
                        messageHandler.sendMessageWithKeyboardMarkup(chatId, ERROR,
                                KeyboardMarkupProvider.inlineContinueButtonToMainMenu());
                        return;
                    }
                    boolean isAdmin = user.getSelectedGroup().getAdmins().stream().anyMatch(u -> u.getId() == user.getId());
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, "Меню группы",
                            KeyboardMarkupProvider.showGroupsSettingsMenu(isAdmin));
                }
                else if (messageText.equals(KeyboardText.SHOW_MEMBERS)) {
                    groupHandler.showMembers(chatId);
                }
                else if (messageText.equals(KeyboardText.DELETE_GROUP)) {
                    groupHandler.deleteGroup(chatId, userRepository.findByChatId(chatId).getSelectedGroup());
                }
                else if (messageText.equals(KeyboardText.GIVE_OWNER)) {
                    groupHandler.handleOwnerCommand(chatId);
                }
                else if (messageText.equals(KeyboardText.REMOVE_ADMIN)) {
                    groupHandler.handleRemoveAdminCommand(chatId);
                }
                else if (messageText.equals(KeyboardText.KICK_USER)) {
                    groupHandler.handleKickCommand(chatId);
                }
                else if (messageText.equals(KeyboardText.SET_ADMIN)) {
                    groupHandler.handleSetAdminCommand(chatId);
                }
                else if (messageText.equals(KeyboardText.INVITE_USER)) {
                    groupHandler.handleInviteCommand(chatId);
                }
                else if (messageText.equals("/schedule")) {
                    messageHandler.sendSchedule(chatId, user.getSelectedGroup().getId());
                }
                else {
                    user.setStatus(statusRepository.findById(1));
                    userRepository.save(user);
                    messageHandler.sendMessageWithKeyboardMarkup(chatId,
                            EmojiParser.parseToUnicode("Я такое не знаю :disappointed_relieved:"),
                            KeyboardMarkupProvider.inlineGoBackButton());

                }
            }
        }
        else if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            User user = userRepository.findByChatId(chatId);
            String callbackData = update.getCallbackQuery().getData();

            switch (callbackData) {
                case "BACK_TO_MAIN_MENU": {
                    messageHandler.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, "Назад",
                            KeyboardMarkupProvider.startMenu());
                    return;
                }
                case "BACK_TO_MAIN_GROUPS_MENU": {
                    boolean chosen = groupHandler.groupChosen(user), created = groupHandler.groupCreated(user);
                    messageHandler.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, GROUP_MENU(user),
                            KeyboardMarkupProvider.groupsMenu(chosen, created));
                    return;
                }
                case "BACK_TO_GROUP_MENU": {
                    boolean isAdmin = user.getSelectedGroup().getAdmins().stream().anyMatch(u -> u.getId() == user.getId());
                    messageHandler.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, "Вы вернулись в меню группы",
                            KeyboardMarkupProvider.showGroupsSettingsMenu(isAdmin));
                    return;
                }
                case "BACK_TO_GROUP_SETTING_MENU": {
                    boolean isOwner = user.getSelectedGroup().getOwner().getId() == user.getId();
                    messageHandler.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, "Вы вернулись в управление группой",
                            KeyboardMarkupProvider.groupSettingsMenu(isOwner));
                    return;
                }
                case "CREATE_GROUP": {
                    messageHandler.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                    groupHandler.handleCreateGroupCommand(chatId);
                    return;
                }
                case "SELECT_GROUP": {
                    messageHandler.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                    groupHandler.handleGroupSelectCommand(chatId);
                    return;
                }
                case "GROUP_MENU": {
                    Groupe selectedGroup = user.getSelectedGroup();
                    boolean isAdmin = selectedGroup.getAdmins().stream().anyMatch(u -> u.getId() == user.getId());
                    messageHandler.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, "Меню группы",
                            KeyboardMarkupProvider.showGroupsSettingsMenu(isAdmin));
                    return;
                }
            }

            switch (user.getStatus().getId()) {
                case 4: {
                    groupHandler.handleGroupSelectInput(chatId, callbackData);
                    messageHandler.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                    break;
                }
            }

            List<Long> data = Arrays.stream(callbackData.split(" ")).mapToLong(Long::parseLong)
                    .boxed().toList(); // данные вида "код <аргументы через пробел>"
            switch (data.get(0).intValue()) {
                case 1: { // код принятия приглашения
                    Groupe group = groupRepository.findById(data.get(1)).get();
                    groupHandler.addUserToGroup(user, group, data.get(2));
                    messageHandler.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                    break;
                }
                case 2: { // код отклонения приглашения
                    Groupe group = groupRepository.findById(data.get(1)).get();
                    groupHandler.declineInvite(user, group, data.get(2));
                    messageHandler.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                    if (group.getMembers().stream().noneMatch(u -> u.getId() == user.getId()))
                        messageHandler.sendMessage(chatId, INVITE_REQUEST_DENIED(group));
                    break;
                }
            }
        }
        else if (update.getMessage().getDocument() != null) {
            long chatId = update.getMessage().getChatId();
            messageHandler.downloadSchedule(update.getMessage(),
                    userRepository.findByChatId(chatId).getSelectedGroup().getId());
        }
    }

    /**
     * Регистрация или обновление информации о пользователе в базе данных.
     * Если пользователь зарегистрирован в боте, то он обновляется в базе данных.
     * Если пользователь не зарегистрирован в боте, то он добавляется в базу данных.
     *
     * @param msg объект сообщения, содержащий информацию о пользователе и чате
     */
    private void registerUser(Message msg) {
        var chatId = msg.getChatId();
        var chat = msg.getChat();
        if (userRepository.findByChatId(chatId) == null) {
            User user = new User(chatId, chat.getFirstName(), chat.getUserName(),
                    new Timestamp(System.currentTimeMillis()));
            user.setStatus(statusRepository.findById(1));
            userRepository.save(user);
            return;
        }
        User user = userRepository.findByChatId(chatId);
        user.setUserName(chat.getUserName());
        user.setFirstName(chat.getFirstName());
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);
    }
}