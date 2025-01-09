package io.tgbot.moaishelper.service;

import io.tgbot.moaishelper.model.*;
import io.tgbot.moaishelper.schedule.ScheduleReader;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
        NotificationsDaemon daemon = new NotificationsDaemon((List<User>) userRepository.findAll(),
                messageHandler, settingsRepository);
        daemon.start();
    }

}