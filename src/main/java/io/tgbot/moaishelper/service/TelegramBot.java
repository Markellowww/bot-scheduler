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

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Начало работы"));
        listOfCommands.add(new BotCommand("/support", "Контактная информация"));
        listOfCommands.add(new BotCommand("/help", "Руководство по использованию бота"));
        listOfCommands.add(new BotCommand("/creategroup", "Создать группу"));
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

    private void handleGroupNameInput(long chatId, String groupName) {
        User creator = userRepository.findByChatId(chatId);
        Groupe groupe = new Groupe(creator, groupName, new Timestamp(System.currentTimeMillis()));
        groupRepository.save(groupe);
        sendMessage(chatId, "Вы успешно создали группу с названием: " + groupe.getName());
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
}
