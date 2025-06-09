package io.tgbot.moaishelper.schedulers.groupFiles;

import io.tgbot.moaishelper.model.GroupFile;
import io.tgbot.moaishelper.repository.GroupFileRepository;

import java.io.File;
import java.util.List;

import static io.tgbot.moaishelper.service.GroupFileHandler.getFile;

public class GroupFilesDaemon extends Thread {
    public GroupFilesDaemon(List<GroupFile> files, GroupFileRepository fileRepository) {
        super(new DaemonTask(files, fileRepository));
        setDaemon(true);
    }

    private record DaemonTask(List<GroupFile> files, GroupFileRepository groupFileRepository) implements Runnable {
        @Override
        public void run() {
            for (GroupFile groupFile : files) {
                deleteFile(groupFile);
            }
        }

        private void deleteFile(GroupFile file) {
            File scheduleFile = getFile(file);
            groupFileRepository.deleteById(file.getId());

            if (scheduleFile.exists())
                scheduleFile.delete();
        }
    }
}