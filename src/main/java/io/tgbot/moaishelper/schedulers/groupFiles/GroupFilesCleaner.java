package io.tgbot.moaishelper.schedulers.groupFiles;

import io.tgbot.moaishelper.repository.GroupFileRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
public class GroupFilesCleaner {
    private final GroupFileRepository groupFileRepository;

    public GroupFilesCleaner(GroupFileRepository groupFileRepository) {
        this.groupFileRepository = groupFileRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    private void cleanGroupFiles() {

    }
}
