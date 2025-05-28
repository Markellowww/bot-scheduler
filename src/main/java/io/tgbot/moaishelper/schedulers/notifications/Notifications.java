package io.tgbot.moaishelper.schedulers.notifications;

import io.tgbot.moaishelper.model.*;
import io.tgbot.moaishelper.repository.UserRepository;
import io.tgbot.moaishelper.repository.UserSettingsRepository;
import io.tgbot.moaishelper.service.MessageHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@EnableScheduling
@Service
class Notifications {

    private final MessageHandler messageHandler;
    private final UserRepository userRepository;
    private final UserSettingsRepository settingsRepository;

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