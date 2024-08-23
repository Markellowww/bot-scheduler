package io.tgbot.moaishelper.service;

import com.vdurmont.emoji.EmojiParser;
import io.tgbot.moaishelper.config.BotConfig;
import io.tgbot.moaishelper.model.*;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Markelloww
 */

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;

    private Map<Long, String> userStates = new HashMap<>();

    final BotConfig config;

    static final String TEXT_ABOUT = EmojiParser.parseToUnicode(
            "Если у вас возникли проблемы, вопросы по работе бота, или есть какие-либо предложения:\n\n" +
            ":arrow_backward:Telegram: @fsbrossii\n\n" +
            ":pushpin:GitHub: https://github.com/Markelloww\n\n" +
            ":e-mail:Почта: markelloww@internet.ru");
    static final String TEXT_IN_DEVELOP = EmojiParser.parseToUnicode("В разработке :disappointed_relieved:");
    static final String TEXT_GROUP_EXISTS = EmojiParser.parseToUnicode("Вы уже создавали ранее группу");
    static final String NO_GROUPS = EmojiParser.parseToUnicode("Вы не состоите ни в одной группе");
    static final String GROUP_NOT_SELECTED = EmojiParser.parseToUnicode("Не выбрана текущая группа");
    static final String NOT_ADMIN = EmojiParser.parseToUnicode("Вы не являетесь администратором данной группы");

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Начало работы"));
        listOfCommands.add(new BotCommand("/support", "Контактная информация"));
        listOfCommands.add(new BotCommand("/help", "Руководство по использованию бота"));
        listOfCommands.add(new BotCommand("/creategroup", "Создать группу"));
        listOfCommands.add(new BotCommand("/selectgroup", "Выбрать группу"));
        listOfCommands.add(new BotCommand("/leavegroup", "Покинуть группу"));
        listOfCommands.add(new BotCommand("/deletegroup", "Покинуть группу"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException _) {
        }
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
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (userStates.containsKey(chatId) && "waiting_for_group_name".equals(userStates.get(chatId))) {
                handleGroupNameInput(chatId, messageText);
            }
            else if (userStates.containsKey(chatId) && userStates.get(chatId).equals("waiting_for_group_selection")) {
                handleGroupSelectInput(chatId, messageText);
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
                    case "/deletegroup": {
                        deleteGroup(chatId, userRepository.findByChatId(chatId).getSelectedGroup());
                        break;
                    }
                    case "/support": {
                        sendMessage(chatId, TEXT_ABOUT);
                        break;
                    }
                    case "/help": {
                        sendMessage(chatId, TEXT_IN_DEVELOP);
                        break;
                    }
                    default: {
                        sendMessage(chatId, EmojiParser.parseToUnicode("Я такое не знаю :disappointed_relieved:"));
                    }
                }
            }
        }
    }

    private void handleCreateGroupCommand(long chatId) {
        long userId = userRepository.findByChatId(chatId).getId();

        if (groupRepository.findByCreatorId(userId) == null) {
            sendMessage(chatId, "Введите название группы");
            userStates.put(chatId, "waiting_for_group_name");
        }
        else {
            sendMessage(chatId, TEXT_GROUP_EXISTS);
        }
    }

    private void handleGroupSelectCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);

        if (!user.getGroupUsers().isEmpty()) {
            userStates.put(chatId, "waiting_for_group_selection");
            addGroupAnswers(chatId, user.getGroupUsers().stream().map(GroupUser::getGroup).toList());
            return;
        }
        sendMessage(chatId, NO_GROUPS);

    }

    private void handleGroupNameInput(long chatId, String groupName) {
        User creator = userRepository.findByChatId(chatId);
        Groupe groupe = new Groupe(creator, groupName, new Timestamp(System.currentTimeMillis()));
        groupRepository.save(groupe);
        creator.setSelectedGroup(groupe);
        userRepository.save(creator);
        sendMessage(chatId, "Вы успешно создали группу с названием: " + groupe.getName());
        userStates.remove(chatId);
    }

    private void handleGroupSelectInput(long chatId, String stringGroupId) {
        User user = userRepository.findByChatId(chatId);
        Groupe selectedGroup = groupRepository.findById(Long.parseLong(stringGroupId)).get();
        user.setSelectedGroup(selectedGroup);
        userRepository.save(user);
        sendMessage(chatId, String.format("Вы выбрали группу \"%s\"", selectedGroup.getName()));
        userStates.remove(chatId);
    }

    private void registerUser(Message msg) {
        var chatId = msg.getChatId();
        var chat = msg.getChat();
        if (userRepository.findByChatId(chatId) == null) { // пользователь не зарегистрирован
            User user = new User(chatId, chat.getFirstName(), chat.getUserName(),
                    new Timestamp(System.currentTimeMillis())); // создание нового пользователя
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
        System.out.println(inlineKeyboardMarkup);
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
     * @author: YDKrivoshey
     */
    private void leaveGroup(long chatId, Groupe selectedGroup) {
        if (selectedGroup == null) {
            sendMessage(chatId, GROUP_NOT_SELECTED);
            return;
        }
        User user = userRepository.findByChatId(chatId);
        if (user.getId() == selectedGroup.getCreator().getId() && selectedGroup.getMembers().size() > 1) {
            sendMessage(chatId, "Перед выходом необходимо передать права владельца другому пользователю");
            return;
        }
        user.setSelectedGroup(null); // обнуление у пользователя выбранной группы
        selectedGroup.removeMember(user); // удаление пользователя из группы
        userRepository.save(user);
        if (!selectedGroup.getMembers().isEmpty())
            groupRepository.save(selectedGroup); // обновление связей в группе
        else
            groupRepository.deleteById(selectedGroup.getId()); // удаление пустой группы
        sendMessage(chatId, "Вы успешно вышли из группы!");
    }

    /**
     * Удаляет группу из базы данных и обнуляет все связи с ней
     * Необходимо быть админом группы
     * @param chatId идентификатор чата
     * @param selectedGroup группа, которую надо удалить
     * @author: YDKrivoshey
     */
    private void deleteGroup(long chatId, Groupe selectedGroup) {
        if (selectedGroup != null) {
            User user = userRepository.findByChatId(chatId);
            if (selectedGroup.getAdmins().stream().anyMatch(admin -> admin.getId() == user.getId())) {
                for (User groupUser : selectedGroup.getMembers()) {
                    if (user.getSelectedGroup().getId() == selectedGroup.getId()) {
                        groupUser.setSelectedGroup(null); // если эта группа выбрана у ее пользователей
                        userRepository.save(groupUser);
                    }
                }
                groupRepository.deleteById(selectedGroup.getId()); // полное удаление группы
            }
            else
                sendMessage(chatId, NOT_ADMIN);
        }
        else
            sendMessage(chatId, GROUP_NOT_SELECTED);
    }

}
