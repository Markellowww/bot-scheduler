package io.tgbot.moaishelper.service;

import io.tgbot.moaishelper.model.*;
import io.tgbot.moaishelper.schedule.ScheduleReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@EnableScheduling
@Service
class Notifications {

    final MessageHandler messageHandler;
    final UserRepository userRepository;
    final UserSettingsRepository settingsRepository;

    private Notifications(@Lazy MessageHandler messageHandler, @Lazy UserRepository userRepository,
                          @Lazy UserSettingsRepository settingsRepository) {
        this.messageHandler = messageHandler;
        this.userRepository = userRepository;
        this.settingsRepository = settingsRepository;
    }

    @Scheduled(cron = "0 0/15 * * * *")
    private void notifyUsers() {
        ZonedDateTime now = ZonedDateTime.now();
        List<User> users = (List<User>) userRepository.findAll();
        for (User user : users) {
            UserSettings settings = settingsRepository.findById(user.getId());
            if (settings.isNotificationsEnabled() && isAppropriateTime(now, settings)) {

                Groupe group = user.getSelectedGroup();
                if (group != null) {
                    messageHandler.sendMessageWithKeyboardMarkupAndParseMode(user.getChatId(),
                            String.format("Расписание группы %s на сегодня:\n%s", group.getName(),
                                    ScheduleReader.todaySchedule(group.getId(), ZoneId.of(settings.getTimeZoneId()))),
                            null);
                }
            }
        }
    }

    private boolean isAppropriateTime(ZonedDateTime time, UserSettings settings) {
        ZonedDateTime zoneTime = time.withZoneSameInstant(ZoneId.of(settings.getTimeZoneId()));

        return zoneTime.getHour() == settings.getNotificationHours() &&
                zoneTime.getMinute() == settings.getNotificationMinutes();
    }
}