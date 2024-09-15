package io.tgbot.moaishelper.schedule;

import com.google.gson.Gson;

import static io.tgbot.moaishelper.schedule.DayWeek.dayOfWeek;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @Authors: Markelloww & YDK
 */

public class ScheduleReader {
    public static String todaySchedule(long groupId) {
        try {
            int week = DayWeek.weekNum();
            String name = getFilename(week);
            Reader reader = Files.newBufferedReader(Paths.get(String.format("src/main/resources/groups/%d/%s.json",
                    groupId, name)));
            Gson gson = new Gson();
            short day = DayWeek.dayOfWeek();
            Schedule schedule = gson.fromJson(reader, Schedule.class);
            return schedule.getDayLessons(dayOfWeek(day));
        }
        catch (IOException _) {
            return "Файл с расписанием не добавлен";
        }
    }

    public static String tomorrowSchedule(long groupId) {
        try {
            short day = DayWeek.dayOfWeek();
            int week = (DayWeek.weekNum() + day / 6) % 2;
            day = (short) ((day + 1) % 7);
            String name = getFilename(week);
            Reader reader = Files.newBufferedReader(Paths.get(String.format("src/main/resources/groups/%d/%s.json",
                    groupId, name)));
            Gson gson = new Gson();
            Schedule schedule = gson.fromJson(reader, Schedule.class);
            return schedule.getDayLessons(dayOfWeek(day));
        }
        catch (IOException _) {
            return "Файл с расписанием не добавлен";
        }
    }

    /**
     * Возвращает текст с расписанием на данную неделю
     * @param groupId идентификатор группы
     * @return текст расписания на текущую неделю
     */
    public static String thisWeekSchedule(long groupId) {
        try {
            int week = DayWeek.weekNum();
            String name = getFilename(week);
            Reader reader = Files.newBufferedReader(Paths.get(String.format("src/main/resources/groups/%d/%s.json",
                    groupId, name)));
            Gson gson = new Gson();
            Schedule schedule = gson.fromJson(reader, Schedule.class);
            return schedule.show();
        }
        catch (IOException _) {
            return "Файл с расписанием не добавлен";
        }
    }

    /**
     * Возвращает текст с расписанием на данную неделю
     * @param groupId идентификатор группы
     * @return текст расписания на следующую неделю
     */
    public static String nextWeekSchedule(long groupId) {
        try {
            int week = (DayWeek.weekNum() + 1) % 2;
            String name = getFilename(week);
            Reader reader = Files.newBufferedReader(Paths.get(String.format("src/main/resources/groups/%d/%s.json",
                    groupId, name)));

            Gson gson = new Gson();
            Schedule schedule = gson.fromJson(reader, Schedule.class);
            return schedule.show();
        }
        catch (IOException _) {
            return "Файл с расписанием не добавлен";
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