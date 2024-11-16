package io.tgbot.moaishelper.schedule;

import com.google.gson.Gson;

import static io.tgbot.moaishelper.schedule.DayWeek.dayOfWeek;
import static io.tgbot.moaishelper.text.Info.SCHEDULE_EMPTY;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;

/**
 * @Authors: Markelloww & YDK
 */
public class ScheduleReader {
    /**
     * Возвращает текст с расписанием на сегодня
     * @param groupId идентификатор группы
     * @return текст расписания на сегодня
     */
    public static String todaySchedule(long groupId, ZoneId zoneId) {
        try {
            int week = DayWeek.weekNum(zoneId);
            String name = getFilename(week);
            Reader reader = Files.newBufferedReader(Paths.get(String.format("src/main/resources/groups/%d/%s.json",
                    groupId, name)));
            Gson gson = new Gson();
            short day = DayWeek.dayOfWeek(zoneId);
            Schedule schedule = gson.fromJson(reader, Schedule.class);
            return schedule.getDayLessons(dayOfWeek(day));
        }
        catch (IOException _) {
            return SCHEDULE_EMPTY;
        }
    }

    /**
     * Возвращает текст с расписанием на завтра
     * @param groupId идентификатор группы
     * @return текст расписания на завтра
     */
    public static String tomorrowSchedule(long groupId, ZoneId zoneId) {
        try {
            short day = DayWeek.dayOfWeek(zoneId);
            int week = (DayWeek.weekNum(zoneId) + day / 6) % 2;
            day = (short) ((day + 1) % 7);
            String name = getFilename(week);
            Reader reader = Files.newBufferedReader(Paths.get(String.format("src/main/resources/groups/%d/%s.json",
                    groupId, name)));
            Gson gson = new Gson();
            Schedule schedule = gson.fromJson(reader, Schedule.class);
            return schedule.getDayLessons(dayOfWeek(day));
        }
        catch (IOException _) {
            return SCHEDULE_EMPTY;
        }
    }

    /**
     * Возвращает текст с расписанием на данную неделю
     * @param groupId идентификатор группы
     * @return текст расписания на текущую неделю
     */
    public static String thisWeekSchedule(long groupId, ZoneId zoneId) {
        try {
            int week = DayWeek.weekNum(zoneId);
            String name = getFilename(week);
            Reader reader = Files.newBufferedReader(Paths.get(String.format("src/main/resources/groups/%d/%s.json",
                    groupId, name)));
            Gson gson = new Gson();
            Schedule schedule = gson.fromJson(reader, Schedule.class);
            return schedule.show();
        }
        catch (IOException _) {
            return SCHEDULE_EMPTY;
        }
    }

    /**
     * Возвращает текст с расписанием на данную неделю
     * @param groupId идентификатор группы
     * @return текст расписания на следующую неделю
     */
    public static String nextWeekSchedule(long groupId, ZoneId zoneId) {
        try {
            int week = (DayWeek.weekNum(zoneId) + 1) % 2;
            String name = getFilename(week);
            Reader reader = Files.newBufferedReader(Paths.get(String.format("src/main/resources/groups/%d/%s.json",
                    groupId, name)));

            Gson gson = new Gson();
            Schedule schedule = gson.fromJson(reader, Schedule.class);
            return schedule.show();
        }
        catch (IOException _) {
            return SCHEDULE_EMPTY;
        }
    }

    public static Schedule getSchedule(long groupId, ZoneId zoneId, final boolean thisWeek) {
        int week = thisWeek ? DayWeek.weekNum(zoneId) : ((DayWeek.weekNum(zoneId) + 1) % 2);
        try {
            String name = getFilename(week);
            Reader reader = Files.newBufferedReader(Paths.get(String.format("src/main/resources/groups/%d/%s.json",
                    groupId, name)));

            return new Gson().fromJson(reader, Schedule.class);
        }
        catch (IOException _) {
            return null;
        }
    }

    /**
     * Возвращает название файла расписания
     * @param week четность недели (0 - числитель, 1 - знаменатель)
     * @return название недели
     */
    private static String getFilename(int week) throws IOException {
        return week == 0 ? "Числитель" : "Знаменатель";
    }
}