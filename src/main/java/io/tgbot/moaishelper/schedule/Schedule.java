package io.tgbot.moaishelper.schedule;

import com.vdurmont.emoji.EmojiParser;
import io.tgbot.moaishelper.parser.TimeParser;
import lombok.Getter;

import java.io.IOException;
import java.util.*;

/**
 * @Authors: Markelloww & YDK
 */
public class Schedule {

    private Map<String, DayLessons> schedule;

    public Schedule() {
        this.schedule = new LinkedHashMap<>();
    }

    // СДЕЛАТЬ -->
    public void updateLessonName(String dayOfWeek, int lessonOrder, String newLessonName) {
        DayLessons day = schedule.get(dayOfWeek);

        Lesson lessonToUpdate = day.lessonList.get(lessonOrder - 1);

        String newTime = ""; // СДЕЛАТЬ ВЫКАЧКУ ИЗ JSON метод в schedulehandler: getTimeBySchedule

        lessonToUpdate.time = newTime;
        lessonToUpdate.lessonName = newLessonName;
    }

    public void updateTeacherName(String dayOfWeek, int lessonOrder, String newLessonName) {
    }

    public void updateAuditorium(String dayOfWeek, int lessonOrder, String newLessonName) {
    }
    // <-- СДЕЛАТЬ

    public List<String> getLessonNames(String dayOfWeek, String path) throws IOException {
        DayLessons day = schedule.getOrDefault(dayOfWeek, new DayLessons());

        ArrayList<String> lessonNames = new ArrayList<>();
        for (Lesson lesson : day.lessonList) {
            String lessonName = String.format("%s. " + lesson.getLessonName(),
                    TimeParser.getOrderByTime(path, lesson.time));
            lessonNames.add(lessonName);
        }

        return lessonNames;
    }

    /**
     * Добавляет день недели в расписание.
     * @param dayOfWeek название дня недели
     */
    public void addDay(String dayOfWeek) {
        schedule.putIfAbsent(dayOfWeek, new DayLessons());
    }

    public String getDayLessonsToPrint(String dayOfWeek) {
        DayLessons day = schedule.getOrDefault(dayOfWeek, new DayLessons());
        return day.show(dayOfWeek);
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

    public void removeLessonByOrder(String dayOfWeek, String order, long groupId) {
        DayLessons day = schedule.getOrDefault(dayOfWeek, new DayLessons());
        String path = String.format("src/main/resources/groups/%d/Время.json", groupId);

        day.lessonList.removeIf(lesson -> {
            try {
                return TimeParser.getOrderByTime(path, lesson.time).equals(order);
            }
            catch (IOException _) {
                return false;
            }
        });
    }

    /**
     * Выводит расписание за все дни
     */
    public String show() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, DayLessons> daySchedule: schedule.entrySet()) {
            builder.append(daySchedule.getValue().show(daySchedule.getKey()));
            builder.append("\n");
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
         * Печатает список уроков за каждый день.
         */
        public String show(String dayOfWeek) {
            String tab = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";
            if (lessonList.isEmpty()) {
                return String.format("<b>%s:</b>\nНет занятий.\n", dayOfWeek.toUpperCase());
            }
            StringBuilder builder = new StringBuilder();
            builder.append(String.format("<b>%s</b>\n", dayOfWeek.toUpperCase()));
            for (Lesson lesson : lessonList) {
                if (lessonList.getLast() == lesson) {
                    builder.append(String.format("%s\n",
                            "┃ \n" +
                                    "┗━━━" + EmojiParser.parseToUnicode(":books: ") + String.format("<i>%s</i>\n",
                                    lesson.lessonName) +
                                    tab + "┣━━━" + EmojiParser.parseToUnicode(":clock3: ") + lesson.time + "\n" +
                                    tab + "┣━━━" + EmojiParser.parseToUnicode(":man_teacher: ") + lesson.teacher +
                                    "\n" +
                                    tab + "┗━━━" + EmojiParser.parseToUnicode(":school: ") + lesson.auditorium));
                }
                else {
                    builder.append(String.format("%s\n", lesson));
                }
            }
            return builder.toString();
        }
    }

    private static class Lesson {
        @Getter
        private String lessonName;
        private String teacher;
        private String time;
        private String auditorium;

        public Lesson() {
        }

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
            String tab = "\t\t\t\t\t\t\t\t\t\t\t\t";
            return "┃ \n" +
                    "┣━━━" +  EmojiParser.parseToUnicode(":books: ") + String.format("<i>%s</i>\n", lessonName) +
                    "┃" + tab + "┣━━━" + EmojiParser.parseToUnicode(":clock3: ") + time + "\n" +
                    "┃" + tab + "┣━━━" + EmojiParser.parseToUnicode(":man_teacher: ") + teacher + "\n" +
                    "┃" + tab + "┗━━━" + EmojiParser.parseToUnicode(":school: ") + auditorium;
        }
    }
}