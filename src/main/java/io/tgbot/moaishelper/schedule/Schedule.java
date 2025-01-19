package io.tgbot.moaishelper.schedule;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vdurmont.emoji.EmojiParser;
import io.tgbot.moaishelper.parser.TimeParser;
import lombok.Getter;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @Authors: Markelloww & YDK
 */
public class Schedule {

    private Map<String, DayLessons> schedule;

    public Schedule() {
        this.schedule = new LinkedHashMap<>();
    }

    public void updateLessonName(String dayOfWeek, int lessonOrder, String newLessonName) {
        DayLessons day = schedule.get(dayOfWeek);
        Lesson lessonToUpdate = day.lessonList.stream().filter(lesson -> lesson.lessonNumber == lessonOrder).
                findFirst().orElse(null);

        if (lessonToUpdate == null) {
            day.addLesson(newLessonName, lessonOrder, "", "");
        }
        else
            lessonToUpdate.lessonName = newLessonName;
    }

    public void updateTeacherName(String dayOfWeek, int lessonOrder, String newTeacher) {
        DayLessons day = schedule.get(dayOfWeek);
        Lesson lessonToUpdate = day.lessonList.stream().filter(lesson -> lesson.lessonNumber == lessonOrder).
                findFirst().orElse(null);

        if (lessonToUpdate == null) {
            day.addLesson("", lessonOrder, "", newTeacher);
        }
        else
            lessonToUpdate.teacher = newTeacher;

    }

    public void updateAuditorium(String dayOfWeek, int lessonOrder, String newAuditorium) {
        DayLessons day = schedule.get(dayOfWeek);
        Lesson lessonToUpdate = day.lessonList.stream().filter(lesson -> lesson.lessonNumber == lessonOrder).
                findFirst().orElse(null);

        if (lessonToUpdate == null) {
            day.addLesson("", lessonOrder, newAuditorium, "");
        }
        else
            lessonToUpdate.auditorium = newAuditorium;

    }

    public List<String> getLessonNames(long groupId, String dayOfWeek, String path) throws IOException {
        DayLessons day = schedule.getOrDefault(dayOfWeek, new DayLessons());

        ArrayList<String> lessonNames = new ArrayList<>();
        for (Lesson lesson : day.lessonList) {
            String lessonName = String.format("%s. " + lesson.getLessonName(),
                    TimeParser.getOrderByTime(path, lesson.getTime(groupId)));
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

    public String getDayLessonsToPrint(String dayOfWeek, long groupId) {
        DayLessons day = schedule.getOrDefault(dayOfWeek, new DayLessons());
        return day.show(dayOfWeek, groupId);
    }

    /**
     * Добавляет урок в указанный день недели.
     *
     * @param dayOfWeek название дня недели
     * @param lessonName название урока
     * @param lessonNumber номер урока
     * @param auditorium номер кабинета
     * @param teacher преподаватель
     *
     */
    public void addLesson(String dayOfWeek, String lessonName, Integer lessonNumber, String auditorium, String teacher) {
        schedule.get(dayOfWeek).addLesson(lessonName, lessonNumber, auditorium, teacher);
    }

    public void removeLessonByOrder(String dayOfWeek, String order, long groupId) {
        DayLessons day = schedule.getOrDefault(dayOfWeek, new DayLessons());
        String path = String.format("src/main/resources/groups/%d/Время.json", groupId);
        if (day.lessonList.isEmpty()) {
            return;
        }
        day.lessonList.removeIf(lesson -> {
            try {
                String lessonTime = lesson.getTime(groupId);
                if (lessonTime != null) {
                    return TimeParser.getOrderByTime(path, lessonTime).equals(order);
                }
            } catch (IOException _) {}
            return false;
        });
    }

    /**
     * Выводит расписание за все дни
     */
    public String show(long groupId) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, DayLessons> daySchedule: schedule.entrySet()) {
            builder.append(daySchedule.getValue().show(daySchedule.getKey(), groupId));
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
         * @param lessonNumber номер урока
         * @param auditorium номер кабинета
         * @param teacher преподаватель
         */
        public void addLesson(String lessonName, Integer lessonNumber, String auditorium, String teacher) {
            Lesson lesson = new Lesson(lessonName, lessonNumber, auditorium, teacher);
            lessonList.add(lesson);
            lessonList.sort(Lesson.lessonNumberComparator);
        }

        /**
         * Печатает список уроков за каждый день.
         */
        public String show(String dayOfWeek, long groupId) {
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
                                    tab + "┣━━━" + EmojiParser.parseToUnicode(":clock3: ") +
                                    lesson.getTime(groupId) +
                                    "\n" +
                                    tab + "┣━━━" + EmojiParser.parseToUnicode(":man_teacher: ") + lesson.teacher +
                                    "\n" +
                                    tab + "┗━━━" + EmojiParser.parseToUnicode(":school: ") + lesson.auditorium));
                }
                else {
                    builder.append(String.format("%s\n", lesson.toString(groupId)));
                }
            }
            return builder.toString();
        }
    }

    private static class Lesson {
        @Getter
        private String lessonName;
        private String teacher;
        private Integer lessonNumber;
        private String auditorium;

        public Lesson() {
        }

        public Lesson(String lessonName, Integer lessonNumber, String auditorium, String teacher) {
            this.lessonName = lessonName;
            this.teacher = teacher;
            this.lessonNumber = lessonNumber;
            this.auditorium = auditorium;
        }

        /**
         * Переопределение метода toString для представления объекта Lesson в виде строки.
         *
         * @return строковое представление объекта Lesson
         */
        public String toString(long groupId) {
            String tab = "\t\t\t\t\t\t\t\t\t\t\t\t";
            return "┃ \n" +
                    "┣━━━" +  EmojiParser.parseToUnicode(":books: ") + String.format("<i>%s</i>\n", lessonName) +
                    "┃" + tab + "┣━━━" + EmojiParser.parseToUnicode(":clock3: ") + getTime(groupId) + "\n" +
                    "┃" + tab + "┣━━━" + EmojiParser.parseToUnicode(":man_teacher: ") + teacher + "\n" +
                    "┃" + tab + "┗━━━" + EmojiParser.parseToUnicode(":school: ") + auditorium;
        }

        public String getTime(long groupId) {
            try(Reader reader = Files.newBufferedReader(Paths.get(String.format(
                    "src/main/resources/groups/%d/Время.json", groupId)))) {
                Gson gson = new Gson();

                Type type = new TypeToken<Map<String, String>>(){}.getType();
                Map<String, String> timeMap = gson.fromJson(reader, type);
                return timeMap.get(String.valueOf(lessonNumber));
            }
            catch (IOException _) {}

            return "";
        }

        public static Comparator<Lesson> lessonNumberComparator = new Comparator<Lesson>() {
            @Override
            public int compare(Lesson l1, Lesson l2) {
                return l1.lessonNumber.compareTo(l2.lessonNumber);
            }
        };
    }
}