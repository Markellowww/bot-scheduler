package io.tgbot.moaishelper.service;

/*
 * @Author: Markelloww
 * Date: 28.08.2024
 */

import com.vdurmont.emoji.EmojiParser;
import io.tgbot.moaishelper.keyboard.KeyboardMarkupProvider;
import io.tgbot.moaishelper.model.GroupRepository;
import io.tgbot.moaishelper.model.StatusRepository;
import io.tgbot.moaishelper.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class MessageHandler {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    TelegramBot bot;


    /**
     * Отравляет начальное сообщение со стартовой клавиатурой.
     *
     * @param chatId идентификатор чата
     * @param firstName имя человека
     */
    protected void startCommandReceived(long chatId, String firstName) {
        String answer = EmojiParser.parseToUnicode("Привет:v:, " + firstName + ", это МОАИС-Helper!\n" +
                "Для начала работы ознакомьтесь с руководством:closed_book:");
        sendMessageWithKeyboardMarkup(chatId, answer, KeyboardMarkupProvider.startMenu());
    }

    /**
     * Отправляет текстовое сообщение в указанный чат с клавиатурой.
     *
     * @param chatId идентификатор чата, в который будет отправлено сообщение
     * @param textToSend текст сообщения, которое нужно отправить
     * @param keyboardMarkup клавиатура, прикрепляемая к сообщению
     */
    protected void sendMessageWithKeyboardMarkup(long chatId, String textToSend, ReplyKeyboard keyboardMarkup) {
        SendMessage message = createSendMassage(chatId, textToSend);
        message.setReplyMarkup(keyboardMarkup);
        try {
            bot.execute(message);
        } catch (TelegramApiException _) {}
    }

    /**
     * Создает объект SendMessage с указанными параметрами для отправки сообщения.
     *
     * @param chatId идентификатор чата, в который будет отправлено сообщение
     * @param textToSend текст сообщения, которое нужно отправить
     * @return объект SendMessage, настроенный с указанными параметрами
     */
    protected SendMessage createSendMassage(long chatId, String textToSend) {
        SendMessage message =  new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        return message;
    }

    /**
     * Отправляет текстовое сообщение в указанный чат без клавиатуры.
     *
     * @param chatId идентификатор чата, в который будет отправлено сообщение
     * @param textToSend текст сообщения, которое нужно отправить
     */
    protected void sendMessage(long chatId, String textToSend) {
        SendMessage message = createSendMassage(chatId,textToSend);
        try {
            bot.execute(message);
        } catch (TelegramApiException _) {}
    }

    /**
     * Удаляет сообщение в чате.
     *
     * @param chatId идентификатор чата, где находится сообщение
     * @param messageId идентификатор удаляемого сообщения
     */
    protected void deleteMessage(long chatId, Integer messageId) {
        try {
            bot.execute(new DeleteMessage(String.valueOf(chatId), messageId));
        } catch (TelegramApiException _) {}
    }

    /**
     * Проверяет строку на наличие у нее первого символа равного '@'.
     *
     * @param message исходное сообщение, которое необходимо проверить
     * @return строку без начального '@', если он присутствует, иначе исходную строку
     */
    protected String checkForAtInMessage(String message) {
        if (message.charAt(0) == '@') {
            return message.substring(1);
        }
        return message;
    }
}
