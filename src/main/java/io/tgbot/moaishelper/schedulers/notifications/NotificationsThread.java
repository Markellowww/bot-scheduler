package io.tgbot.moaishelper.schedulers.notifications;

import io.tgbot.moaishelper.model.Groupe;
import io.tgbot.moaishelper.model.User;
import io.tgbot.moaishelper.model.UserSettings;
import io.tgbot.moaishelper.repository.UserSettingsRepository;
import io.tgbot.moaishelper.schedule.ScheduleReader;
import io.tgbot.moaishelper.service.MessageHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class NotificationsThread extends Thread {
    public NotificationsThread(List<User> users, MessageHandler messageHandler,
                               UserSettingsRepository settingsRepository) {
        super(new DaemonTask(users, messageHandler, settingsRepository));
    }

    private record DaemonTask(List<User> users, MessageHandler messageHandler,
                              UserSettingsRepository settingsRepository) implements Runnable {

        @Override
            public void run() {
                ZonedDateTime now = ZonedDateTime.now();
                for (User user : users) {
                    UserSettings settings = settingsRepository.findById(user.getId());
                    if (settings.isNotificationsEnabled() && isAppropriateTime(now, settings)) {

                        Groupe group = user.getSelectedGroup();
                        if (group != null) {
                            messageHandler.sendMessageWithKeyboardMarkupAndParseMode(user.getChatId(),
                                    String.format("Расписание группы %s на сегодня:\n%s", group.getName(),
                                            ScheduleReader.todaySchedule(group.getId(),
                                                    ZoneId.of(settings.getTimeZoneId()))),
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
}