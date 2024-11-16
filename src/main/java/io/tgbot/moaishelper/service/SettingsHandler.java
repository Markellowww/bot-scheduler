package io.tgbot.moaishelper.service;

import com.vdurmont.emoji.EmojiParser;
import io.tgbot.moaishelper.keyboard.KeyboardMarkupProvider;
import io.tgbot.moaishelper.model.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramBot;

import java.util.Map;

import static io.tgbot.moaishelper.text.Info.TIME_SELECTION;
import static io.tgbot.moaishelper.text.Info.USER_NOTIFICATION_SETTINGS;

@Component
public class SettingsHandler {
    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final TelegramBot bot;
    private final MessageHandler messageHandler;

    private final Map<Integer, String> zonesByHourDif = Map.ofEntries(Map.entry(-1, "Europe/Kaliningrad"),
            Map.entry(0, "Europe/Moscow"), Map.entry(1, "Europe/Samara"),
            Map.entry(2, "Asia/Yekaterinburg"), Map.entry(3, "Asia/Omsk"),
            Map.entry(4, "Asia/Novosibirsk"), Map.entry(5, "Asia/Irkutsk"),
            Map.entry(6, "Asia/Yakutsk"), Map.entry(7, "Asia/Vladivostok"),
            Map.entry(8, "Asia/Magadan"), Map.entry(9, "Asia/Anadyr"));

    public SettingsHandler(UserRepository userRepository,
                        UserSettingsRepository settingsRepository,
                        TelegramBot bot,
                        MessageHandler messageHandler) {
        this.userRepository = userRepository;
        this.userSettingsRepository = settingsRepository;
        this.bot = bot;
        this.messageHandler = messageHandler;
    }

    public void showNotificationMenu(long chatId, UserSettings settings) {
        boolean notificationsEnabled = settings.isNotificationsEnabled();
        String notificationStatus = settings.isNotificationsEnabled() ?
                "Включены ✅" : EmojiParser.parseToUnicode("Выключены :x:");

        String hours = String.format("%02d", settings.getNotificationHours());
        String minutes = String.format("%02d", settings.getNotificationMinutes());

        String timeSend = hours + ":" + minutes;
        String timeZone = settings.getTimeZoneId();

        messageHandler.sendMessageWithKeyboardMarkup(chatId,
                USER_NOTIFICATION_SETTINGS(notificationStatus, timeSend, timeZone),
                KeyboardMarkupProvider.notificationMenu(notificationsEnabled));
    }

    protected void switchNotifications(User user) {
        UserSettings settings = userSettingsRepository.findById(user.getId());
        settings.setNotificationsEnabled(!settings.isNotificationsEnabled());
        userSettingsRepository.save(settings);
    }

    protected void handleTimeZoneInput(User user, int hourDif) {
        UserSettings settings = userSettingsRepository.findById(user.getId());
        settings.setTimeZoneId(zonesByHourDif.get(hourDif));
        userSettingsRepository.save(settings);
    }

    protected void handleHoursInput(User user, short hours) {
        UserSettings settings = userSettingsRepository.findById(user.getId());
        settings.setNotificationHours(hours);
        userSettingsRepository.save(settings);
        messageHandler.sendMessageWithKeyboardMarkup(user.getChatId(),
                TIME_SELECTION, KeyboardMarkupProvider.timeMenu());
    }

    protected void handleMinutesInput(User user, short minutes) {
        UserSettings settings = userSettingsRepository.findById(user.getId());
        settings.setNotificationMinutes(minutes);
        userSettingsRepository.save(settings);
        messageHandler.sendMessageWithKeyboardMarkup(user.getChatId(),
                TIME_SELECTION, KeyboardMarkupProvider.timeMenu());
    }
}