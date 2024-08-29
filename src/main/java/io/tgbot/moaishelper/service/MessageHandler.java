package io.tgbot.moaishelper.service;

/**
 * @Authors: Markelloww & YDK
 */

import com.vdurmont.emoji.EmojiParser;
import io.tgbot.moaishelper.keyboard.KeyboardMarkupProvider;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;

import static org.apache.commons.io.FileUtils.getFile;

@Component
public class MessageHandler {

    private final TelegramBot bot;

    public MessageHandler(TelegramBot bot) {
        this.bot = bot;
    }

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
     * Присылает файл excel с расписанием. Если его нет, оповещает пользователя об этом.
     * @param chatId идентификатор чата пользователя
     * @param groupId идентификатор группы с расписанием
     */
    protected void sendSchedule(long chatId, long groupId) {
        File scheduleFile = new File(String.format("src/main/resources/groups/%d/Schedule.xlsx", groupId));
        if (scheduleFile.exists()) {
            SendDocument schedule = new SendDocument();
            schedule.setDocument(new InputFile(scheduleFile, "Расписание.xlsx"));
            schedule.setChatId(chatId);
            try {
                bot.execute(schedule);
            } catch (TelegramApiException _) {}
            return;
        }
        sendMessage(chatId, "Расписание не заполнено");
    }

    /**
     * Извлекает из сообщения файл с расписанием и сораняет в группу
     * @param message сообщение с прикрепленным файлом
     */
    protected void downloadSchedule(Message message, long groupId) {
        Document document = message.getDocument();
        long chatId = message.getChatId();
        if (document.getFileName().endsWith(".xlsx")) {
            try {
                GetFile getFile = new GetFile();
                getFile.setFileId(document.getFileId());
                org.telegram.telegrambots.meta.api.objects.File file = bot.execute(getFile);

                bot.downloadFile(file, new
                        java.io.File(String.format("src/main/resources/groups/%d/Schedule.xlsx", groupId)));
            } catch (TelegramApiException _) {
            }
            return;
        }
        sendMessage(chatId, "Неверный формат файла");
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
