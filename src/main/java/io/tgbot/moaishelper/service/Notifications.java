package io.tgbot.moaishelper.service;

import io.tgbot.moaishelper.model.GroupRepository;
import io.tgbot.moaishelper.model.Groupe;
import io.tgbot.moaishelper.model.User;
import io.tgbot.moaishelper.schedule.ScheduleReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

//@EnableScheduling
//@Service
class Notifications {
//    @Autowired
    GroupRepository groupRepository;

    final MessageHandler messageHandler;

    private Notifications(@Lazy MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

//    @Scheduled(cron = "0 0/15 * * * *")
    public void notifyUsers() {
        List<Groupe> groupes = (List<Groupe>) groupRepository.findAll();
        for (Groupe group : groupes) {
            for (User user : group.getMembers()) {
                messageHandler.sendMessage(user.getChatId(), String.format("Расписание группы %s на сегодня:\n%s",
                        group.getName(), ScheduleReader.todaySchedule(group.getId())));
            }
        }
    }

}
