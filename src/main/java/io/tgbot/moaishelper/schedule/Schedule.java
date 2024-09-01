package io.tgbot.moaishelper.schedule;

import com.vdurmont.emoji.EmojiParser;
import lombok.Getter;

import java.util.*;

/**
 * @Authors: Markelloww & YDK
 */

public class Schedule {

    private Map<String, DayLessons> schedule;

    public Schedule() {
        this.schedule = new LinkedHashMap<>();
    }

    /**
     * Добавляет день недели в расписание.
     * @param dayOfWeek название дня недели
     */
    public void addDay(String dayOfWeek) {
        schedule.putIfAbsent(dayOfWeek, new DayLessons());
    }

    public String getDayLessons(String dayOfWeek) {
        DayLessons day = schedule.getOrDefault(dayOfWeek, new DayLessons());
        return day.show();
    }

    /**
     * Добавляет урок в указанный день недели.
     *
     * @param dayOfWeek название дня недели
     * @param lessonName название урока
     * @param time время начала урока
     * @param auditorium номер кабинета
     * @param teacher преподаватель
     *
     */
    public void addLesson(String dayOfWeek, String lessonName, String time, String auditorium, String teacher) {
        schedule.get(dayOfWeek).addLesson(lessonName, time, auditorium, teacher);
    }

    /**
     * Удаляет урок из указанного дня
     *
     * @param dayOfWeek название дня недели
     * @param lessonName название удаляемого урока
     */
    public void removeLesson(String dayOfWeek, String lessonName) {
        schedule.get(dayOfWeek).removeLesson(lessonName);
    }

    /**
     * Удаляет день из общего расписания
     *
     * @param dayOfWeek название удаляемого дня
     */
    public void removeDay(String dayOfWeek) {
        schedule.remove(dayOfWeek);
    }

    /**
     * Выводит расписание за все дни
     */
    public String show() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, DayLessons> daySchedule: schedule.entrySet()) {
            builder.append(String.format("\n%s:\n",daySchedule.getKey()));
            builder.append(daySchedule.getValue().show());
        }
        return builder.toString();
    }

    private static class DayLessons {
        private final List<Lesson> lessonList;

        public DayLessons() {
            this.lessonList = new ArrayList<>();
        }

        /**
         * Добавляет урок в день.
         *
         * @param lessonName название урока
         * @param time время начала урока
         * @param auditorium номер кабинета
         * @param teacher преподаватель
         */
        public void addLesson(String lessonName, String time, String auditorium, String teacher) {
            Lesson lesson = new Lesson(lessonName, time, auditorium, teacher);
            lessonList.add(lesson);
        }

        /**
         * Удаляет урок с данным названием.
         *
         * @param lessonName название урока
         */
        public void removeLesson(String lessonName) {
            lessonList.removeIf(lesson -> lesson.getLessonName().equals(lessonName));
        }

        /**
         * Печатает список уроков за каждый день.
         */
        public String show() {
            if (lessonList.isEmpty()) {
                return "Нет уроков.";
            }
            StringBuilder builder = new StringBuilder();
            for (Lesson lesson : lessonList) {
                builder.append(String.format("%s\n", lesson));
            }
            return builder.toString();
        }
    }

    private static class Lesson {
        @Getter
        private final String lessonName;
        private final String teacher;
        private final String time;
        private final String auditorium;

        public Lesson(String lessonName, String time, String auditorium, String teacher) {
            this.lessonName = lessonName;
            this.teacher = teacher;
            this.time = time;
            this.auditorium = auditorium;
        }

        /**
         * Переопределение метода toString для представления объекта Lesson в виде строки.
         *
         * @return строковое представление объекта Lesson
         */
        @Override
        public String toString() {
            return "----------------------------------------\n" +
                    EmojiParser.parseToUnicode(":books: ") + lessonName + "\n" +
                    EmojiParser.parseToUnicode(" :clock3: ") + time + "\n" +
                    EmojiParser.parseToUnicode(":briefcase: ") + teacher + "\n" +
                    EmojiParser.parseToUnicode(":door: ") + auditorium;
        }
    }
}