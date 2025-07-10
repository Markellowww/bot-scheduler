package io.tgbot.moaishelper.schedulers.groupFiles;

import io.tgbot.moaishelper.model.GroupFile;
import io.tgbot.moaishelper.repository.GroupFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@EnableScheduling
@Service
public class GroupFilesCleaner {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final GroupFileRepository groupFileRepository;

    public GroupFilesCleaner(GroupFileRepository groupFileRepository) {
        this.groupFileRepository = groupFileRepository;
    }

    @Scheduled(cron = "0 30 12 * * *")
    private void cleanGroupFiles() {
        List<GroupFile> expiredFiles = findExpiredFiles();
        logger.info("found {} expired files. Start clearing", expiredFiles.size());
        GroupFilesThread thread = new GroupFilesThread(expiredFiles, groupFileRepository);
        thread.start();
    }

    private List<GroupFile> findExpiredFiles() {
        return groupFileRepository.findAll().stream()
                .filter(gf -> gf.getCreatedAt().plusDays(gf.getDaysToStore()).isBefore(LocalDate.now()))
                .toList();
    }
}