package io.tgbot.moaishelper.schedule;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Authors: Markelloww & YDK
 */

public class Schedule {

    private Map<String, DayLessons> schedule;

    public Schedule() {
        this.schedule = new HashMap<>();
    }

    /**
     * Добавляет день недели в расписание.
     * @param dayOfWeek название дня недели
     */
    public void addDay(String dayOfWeek) {
        schedule.putIfAbsent(dayOfWeek, new DayLessons());
    }

    /**
     * Добавляет урок в указанный день недели.
     *
     * @param dayOfWeek название дня недели
     * @param lessonName название урока
     * @param startTime время начала урока
     * @param endTime время окончания урока
     * @param cabinet номер кабинета
     */
    public void addLesson(String dayOfWeek, String lessonName, String startTime, String endTime, String cabinet) {
        schedule.get(dayOfWeek).addLesson(lessonName, startTime, endTime, cabinet);
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
    public void show() {
        for (Map.Entry<String, DayLessons> daySchedule: schedule.entrySet()) {
            System.out.println(daySchedule.getKey() + ":");
            daySchedule.getValue().show();
        }
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
         * @param startTime время начала урока
         * @param endTime время окончания урока
         * @param cabinet номер кабинета
         */
        public void addLesson(String lessonName, String startTime, String endTime, String cabinet) {
            Lesson lesson = new Lesson(lessonName , startTime, endTime, cabinet);
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
        public void show() {
            if (lessonList.isEmpty()) {
                System.out.println("Нет уроков.");
            }
            else {
                for (Lesson lesson : lessonList) {
                    System.out.println(lesson);
                }
            }
        }
    }

    private static class Lesson {
        @Getter
        private final String lessonName;
        private final String startTime;
        private final String endTime;
        private final String cabinet;

        public Lesson(String lessonName, String startTime, String endTime, String cabinet) {
            this.lessonName = lessonName;
            this.startTime = startTime;
            this.endTime = endTime;
            this.cabinet = cabinet;
        }

        /**
         * Переопределение метода toString для представления объекта Lesson в виде строки.
         *
         * @return строковое представление объекта Lesson
         */
        @Override
        public String toString() {
            return "Предмет: " + lessonName + ". Время: " + startTime + " " + endTime + ". Кабинет: " + cabinet;
        }
    }
}