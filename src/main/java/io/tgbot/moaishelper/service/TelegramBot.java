package io.tgbot.moaishelper.service;

import com.vdurmont.emoji.EmojiParser;
import io.tgbot.moaishelper.config.BotConfig;
import io.tgbot.moaishelper.model.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @Authors: Markelloww & YDK
 */

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private StatusRepository statusRepository;

    final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        CommandInitializer.init(listOfCommands);
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        }
        catch (TelegramApiException _) {}
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
            if (user != null && user.getStatus().getId() != 1) { // ожидается ввод каких-то данных
                switch (user.getStatus().getId()) {
                    case 2: {
                        handleGroupNameInput(chatId, messageText);
                        break;
                    }
                    case 3: {
                        handleInviteUserInput(chatId, messageText);
                        break;
                    }
                    default:
                        sendMessage(chatId, Info.NOT_SELECTED_ARGUMENT());
                }
            }
            else {
                switch (messageText) {
                    case "/start": {
                        registerUser(update.getMessage());
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                    }
                    case "/creategroup": {
                        handleCreateGroupCommand(chatId);
                        break;
                    }
                    case "/selectgroup": {
                        handleGroupSelectCommand(chatId);
                        break;
                    }
                    case "/leavegroup": {
                        leaveGroup(chatId, userRepository.findByChatId(chatId).getSelectedGroup());
                        break;
                    }
                    case "/invite": {
                        handleInviteCommand(chatId);
                        break;
                    }
                    case "/deletegroup": {
                        deleteGroup(chatId, userRepository.findByChatId(chatId).getSelectedGroup());
                        break;
                    }
                    case "/support": {
                        sendMessage(chatId, Info.TEXT_ABOUT());
                        break;
                    }
                    case "/help": {
                        sendMessage(chatId, Info.TEXT_IN_DEVELOP());
                        break;
                    }
                    default: {
                        sendMessage(chatId, EmojiParser.parseToUnicode("Я такое не знаю :disappointed_relieved:"));
                    }
                }
            }
        }
        else if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            User user = userRepository.findByChatId(chatId);

            String call_data = update.getCallbackQuery().getData();
            switch (user.getStatus().getId()) {
                case 4: {
                    handleGroupSelectInput(chatId, call_data);
                    break;
                }
            }
        }
    }

    private void handleInviteCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        if (user.getSelectedGroup() != null) {
            Groupe selectedGroup = user.getSelectedGroup();
            if (selectedGroup.getAdmins().stream().anyMatch(admin -> admin.getId() == user.getId())) {
                sendMessage(chatId, "Введите @UserName приглашаемого человека");
                user.setStatus(statusRepository.findById(3));
                userRepository.save(user);
            }
            else
                sendMessage(chatId, Info.NOT_ADMIN());
        }
        else
            sendMessage(chatId, Info.GROUP_NOT_SELECTED());
    }

    private void handleInviteUserInput(long chatId, String invitedUserName) {
        User user = userRepository.findByChatId(chatId);
        user.setStatus(statusRepository.findById(1));

        if (userRepository.findByUserName(invitedUserName) != null) {
            User invitedUser = userRepository.findByUserName(invitedUserName);
            Groupe group = user.getSelectedGroup();
            if (group.getMembers().stream().anyMatch(u -> u.getId() == invitedUser.getId())) {
                sendMessage(chatId, "Пользователь уже состоит в группе");
                return;
            }
            group.addMember(invitedUser, false);
            groupRepository.save(group);
            sendMessage(chatId, "Пользователь успешно добавлен!");
        }
        else
            sendMessage(chatId, Info.USER_NOT_EXISTS());
    }

    private void handleCreateGroupCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        if (groupRepository.findByOwnerId(user.getId()) == null) {
            sendMessage(chatId, "Введите название группы");
            user.setStatus(statusRepository.findById(2));
            userRepository.save(user);
        }
        else {
            sendMessage(chatId, Info.TEXT_GROUP_EXISTS());
        }
    }

    private void handleGroupNameInput(long chatId, String groupName) {
        User creator = userRepository.findByChatId(chatId);
        Groupe groupe = new Groupe(creator, groupName, new Timestamp(System.currentTimeMillis()));
        System.out.println(groupe.getId());
        groupRepository.save(groupe);

        new File(String.format("src/main/resources/groups/%d",
                groupRepository.findByOwnerId(creator.getId()).getId())).mkdirs();

        creator.setSelectedGroup(groupe);
        creator.setStatus(statusRepository.findById(1));
        userRepository.save(creator);
        sendMessage(chatId, "Вы успешно создали группу с названием: " + groupe.getName());
    }

    private void handleGroupSelectCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);

        if (!user.getGroupUsers().isEmpty()) {
            user.setStatus(statusRepository.findById(4));
            userRepository.save(user);
            addGroupAnswers(chatId, user.getGroupUsers().stream().map(GroupUser::getGroup).toList());
            return;
        }
        sendMessage(chatId, Info.NO_GROUPS());
    }

    private void handleGroupSelectInput(long chatId, String stringGroupId) {
        System.out.println(stringGroupId);
        User user = userRepository.findByChatId(chatId);
        Groupe selectedGroup = groupRepository.findById(Long.parseLong(stringGroupId)).get();
        user.setSelectedGroup(selectedGroup);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);
        sendMessage(chatId, String.format("Вы выбрали группу \"%s\"", selectedGroup.getName()));
    }

    private void registerUser(Message msg) {
        var chatId = msg.getChatId();
        var chat = msg.getChat();
        if (userRepository.findByChatId(chatId) == null) { // пользователь не зарегистрирован
            User user = new User(chatId, chat.getFirstName(), chat.getUserName(),
                    new Timestamp(System.currentTimeMillis())); // создание нового пользователя
            user.setStatus(statusRepository.findById(1));
            userRepository.save(user);
            return;
        }
        User user = userRepository.findByChatId(chatId); // иначе обновление данных об имеющемся пользователе
        user.setUserName(chat.getUserName());
        user.setFirstName(chat.getFirstName());
        userRepository.save(user);
    }

    private void startCommandReceived(long chatId, String firstName) {
        String answer = EmojiParser.parseToUnicode("Привет:v:, " + firstName + ", это МОАИС-Helper!\n" +
                "Для начала работы ознакомьтесь с руководством:closed_book: (/help).");
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message =  new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException _) {
        }
    }

    private void addGroupAnswers(long chatId, List<Groupe> groups) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (int i = 0; i < Math.ceil(groups.size() / 3.0); i++) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            for (int j = 0; j < 3 && 3 * i + j < groups.size(); j++) {
                Groupe groupe = groups.get(3 * i + j);
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(groupe.getName());
                button.setCallbackData(String.valueOf(groupe.getId()));
                buttons.add(button);
            }
            rows.add(buttons);
        }
        inlineKeyboardMarkup.setKeyboard(rows);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите группу");
        message.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException _) {
        }
    }

    /**
     * Удаляет пользователя из группы.
     * Если это владелец группы, он должен передать права на группу другому члену группы.
     * Если это последний член группы, будет удаление группы.
     *
     * @param chatId идентификатор чата
     * @param selectedGroup группа для выхода
     */
    private void leaveGroup(long chatId, Groupe selectedGroup) {
        if (selectedGroup == null) {
            sendMessage(chatId, Info.GROUP_NOT_SELECTED());
            return;
        }
        User user = userRepository.findByChatId(chatId);
        if (user.getId() == selectedGroup.getOwner().getId() && selectedGroup.getMembers().size() > 1) {
            sendMessage(chatId, "Перед выходом необходимо передать права владельца другому пользователю");
            return;
        }
        user.setSelectedGroup(null); // обнуление у пользователя выбранной группы
        selectedGroup.removeMember(user); // удаление пользователя из группы
        userRepository.save(user);
        if (!selectedGroup.getMembers().isEmpty())
            groupRepository.save(selectedGroup); // обновление связей в группе
        else {
            groupRepository.deleteById(selectedGroup.getId()); // удаление пустой группы
            try {
                FileUtils.deleteDirectory(new File(String.format("src/main/resources/groups/%d",
                        selectedGroup.getId())));
            }
            catch (IOException _) {
            }
        }
        sendMessage(chatId, "Вы успешно вышли из группы!");
    }

    /**
     * Удаляет группу из базы данных и обнуляет все связи с ней
     * Необходимо быть админом группы
     * @param chatId идентификатор чата
     * @param selectedGroup группа, которую надо удалить
     */
    private void deleteGroup(long chatId, Groupe selectedGroup) {
        if (selectedGroup != null) {
            User user = userRepository.findByChatId(chatId);
            if (selectedGroup.getAdmins().stream().anyMatch(admin -> admin.getId() == user.getId())) {
                for (User groupUser : selectedGroup.getMembers()) {
                    if (user.getSelectedGroup().getId() == selectedGroup.getId()) {
                        groupUser.setSelectedGroup(null); // если эта группа выбрана у ее пользователей
                        userRepository.save(groupUser);
                        sendMessage(chatId, String.format("Группа \"%s\" успешно удалена", selectedGroup.getName()));
                    }
                }
                groupRepository.deleteById(selectedGroup.getId()); // полное удаление группы
                try {
                    FileUtils.deleteDirectory(new File(String.format("src/main/resources/groups/%d",
                            selectedGroup.getId())));
                }
                catch (IOException _) {
                }
            }
            else
                sendMessage(chatId, Info.NOT_ADMIN());
        }
        else
            sendMessage(chatId, Info.GROUP_NOT_SELECTED());
    }

}
