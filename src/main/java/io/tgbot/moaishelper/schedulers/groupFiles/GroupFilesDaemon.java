package io.tgbot.moaishelper.schedulers.groupFiles;

public class GroupFilesDaemon extends Thread {
    public GroupFilesDaemon() {
        super(new DaemonTask());
        setDaemon(true);
    }

    private record DaemonTask() implements Runnable {
        @Override
        public void run() {
            GroupFilesDaemon daemon = new GroupFilesDaemon();
            daemon.start();
        }
    }
}