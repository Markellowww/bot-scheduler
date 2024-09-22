package io.tgbot.moaishelper.service;

import com.vdurmont.emoji.EmojiParser;
import io.tgbot.moaishelper.keyboard.KeyboardMarkupProvider;
import io.tgbot.moaishelper.model.Groupe;
import io.tgbot.moaishelper.model.User;
import io.tgbot.moaishelper.text.Info;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

/**
 * @Authors: Markelloww & YDK
 */

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

    protected void sendMessageWithKeyboardMarkupAndParseMode(long chatId, String textToSend, ReplyKeyboard keyboardMarkup) {
        if (textToSend.length() >= 4096) {
            int index = textToSend.length() / 2;
            String secondHalfText = textToSend.substring(index);
            index += secondHalfText.indexOf("<b>");
            SendMessage firstHalf = createSendMassage(chatId, textToSend.substring(0, index));
            firstHalf.setReplyMarkup(keyboardMarkup);
            firstHalf.setParseMode(ParseMode.HTML);
            SendMessage secondHalf = createSendMassage(chatId, textToSend.substring(index));
            secondHalf.setReplyMarkup(keyboardMarkup);
            secondHalf.setParseMode(ParseMode.HTML);
            try {
                bot.execute(firstHalf);
                bot.execute(secondHalf);
            } catch (TelegramApiException _) {}
        }
        else {
            SendMessage message = createSendMassage(chatId, textToSend);
            message.setReplyMarkup(keyboardMarkup);
            message.setParseMode(ParseMode.HTML);
            try {
                bot.execute(message);
            } catch (TelegramApiException _) {}
        }
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
    protected void uploadSchedule(long chatId, long groupId) {
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

    protected void uploadScheduleTemplate(long chatId) {
        File scheduleFile = new File("src/main/resources/Schedule.xlsx");
        SendDocument schedule = new SendDocument();
        schedule.setDocument(new InputFile(scheduleFile, "Расписание.xlsx"));
        schedule.setChatId(chatId);
        try {
            bot.execute(schedule);
        } catch (TelegramApiException _) {}
    }

    /**
     * Извлекает из сообщения файл с расписанием и сораняет в группу
     * @param message сообщение с прикрепленным файлом
     */
    protected boolean downloadSchedule(Message message, long groupId) {
        Document document = message.getDocument();
        long chatId = message.getChatId();
        if (document.getFileName().endsWith(".xlsx")) {
            try {
                GetFile getFile = new GetFile();
                getFile.setFileId(document.getFileId());
                org.telegram.telegrambots.meta.api.objects.File file = bot.execute(getFile);

                bot.downloadFile(file, new
                        java.io.File(String.format("src/main/resources/groups/%d/Schedule.xlsx", groupId)));
                return true;
            } catch (TelegramApiException _) {
                sendMessage(chatId, Info.ERROR);
            }
            return false;
        }
        sendMessage(chatId, "Неверный формат файла");
        return false;
    }

    /**
     * Отправляет текстовое сообщение в указанный чат без клавиатуры.
     *
     * @param chatId идентификатор чата, в который будет отправлено сообщение
     * @param textToSend текст сообщения, которое нужно отправить
     */
    protected void sendMessage(long chatId, String textToSend) {
        SendMessage message = createSendMassage(chatId, textToSend);
        try {
            bot.execute(message);
        } catch (TelegramApiException _) {}
    }

    protected void sendMessageForAll(Iterable<User> users, String textToSend) {
        for (User user : users) {
            SendMessage message = createSendMassage(user.getChatId(), textToSend);
            try {
                bot.execute(message);
            } catch (TelegramApiException _) {}
        }
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

    public static String notificationMessage(User user, Groupe group, String text) {
        String emoji = EmojiParser.parseToUnicode(":incoming_envelope:");
        return emoji + String.format(" Вам пришло сообщение из группы \"%s\" от пользователя @%s: \n\n", group.getName(), user.getUserName())
                + text;
    }
}
