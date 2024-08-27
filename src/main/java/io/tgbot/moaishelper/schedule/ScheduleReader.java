package io.tgbot.moaishelper.schedule;

import com.google.gson.Gson;
import io.tgbot.moaishelper.service.DayWeek;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ScheduleReader {
    public static void main(String[] args) throws IOException {
        System.out.println(thisWeekSchedule(1));
    }

    public static String todaySchedule(long groupId) {
        try {
            int week = DayWeek.weekNum();
            String name = getFilename(week);
            Reader reader = Files.newBufferedReader(Paths.get(String.format("src/main/resources/groups/%d/%s.json",
                    groupId, name)));
            Gson gson = new Gson();
            int dayOfWeek = DayWeek.dayOfWeek();
            Schedule schedule = gson.fromJson(reader, Schedule.class);
            return "";
        }
        catch (IOException _) {}
        return "";
    }

    public static String tomorrowSchedule(long groupId) {
        try {
            int dayOfWeek = DayWeek.dayOfWeek();
            int week = DayWeek.weekNum() + dayOfWeek / 6;
            dayOfWeek = (dayOfWeek + 1) % 7;
            String name = getFilename(week);
            Reader reader = Files.newBufferedReader(Paths.get(String.format("src/main/resources/groups/%d/%s.json",
                    groupId, name)));
            Gson gson = new Gson();
            Schedule schedule = gson.fromJson(reader, Schedule.class);
        }
        catch (IOException _) {}
        return "";
    }

    public static String thisWeekSchedule(long groupId) {
        try {
            int week = DayWeek.weekNum();
            String name = getFilename(week);
            Reader reader = Files.newBufferedReader(Paths.get(String.format("src/main/resources/groups/%d/%s.json",
                    groupId, name)));
            Gson gson = new Gson();
            Schedule schedule = gson.fromJson(reader, Schedule.class);
            schedule.show();
        }
        catch (IOException _) {}
        return "";
    }

    public static String nextWeekSchedule(long groupId) {
        try {
            int week = (DayWeek.weekNum() + 1) % 2;
            String name = getFilename(week);
            Reader reader = Files.newBufferedReader(Paths.get(String.format("src/main/resources/groups/%d/%s.json",
                    groupId, name)));

            Gson gson = new Gson();
            Schedule schedule = gson.fromJson(reader, Schedule.class);
            schedule.show();
        }
        catch (IOException _) {}
        return "";
    }

    private static String getFilename(int week) throws IOException {
        return week == 0 ? "Числитель" : "Знаменатель";
    }
}
