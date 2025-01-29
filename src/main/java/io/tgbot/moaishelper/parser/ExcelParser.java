package io.tgbot.moaishelper.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.tgbot.moaishelper.schedule.Day;
import io.tgbot.moaishelper.schedule.Schedule;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * @Authors: Markelloww & YDK
 */
public class ExcelParser {
    public static boolean parse(long groupId) {
        if (readLessonTime(groupId))
            return createSchedule(groupId);
        return false;
    }

    private static List<List<List<List<Object>>>> readScheduleTwoColumns(long groupId) {
        String filename = String.format("src/main/resources/groups/%d/Schedule.xlsx", groupId);
        try(XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(filename))) {
            XSSFSheet mySheet = myExcelBook.getSheetAt(0); // первая таблица
            XSSFRow currentRow; // текущая строка из excel

            List<List<List<List<Object>>>> data = new ArrayList<>();
            short cellIndex = 1; // индекс клетки в строке
            for (short week = 0; week < 2; week++, cellIndex += 2) {
                short rowIndex = 3; // текущая строка по индексу (см шаблон)
                List<List<List<Object>>> weekData = new ArrayList<>();

                for (short day = 1; day < 8; day++, rowIndex += 2) {
                    List<List<Object>> dayData = new ArrayList<>();

                    for (int pair = 1; pair < 11; pair++, rowIndex++) {
                        cellIndex = (short) (1 + 5 * week);
                        currentRow = mySheet.getRow(rowIndex);
                        List<Object> subjectData = new ArrayList<>();

                        String subject = currentRow.getCell(cellIndex++).toString();
                        String teacher = currentRow.getCell(cellIndex++).toString(); // данные
                        String auditorium  = currentRow.getCell(cellIndex++).toString();
                        try {
                            auditorium = String.valueOf((int) Float.parseFloat(auditorium));
                        } // нормальный вид для целых чисел
                        catch (NumberFormatException _) {
                        }

                        if (Stream.of(subject, teacher, auditorium).anyMatch(s -> !s.isEmpty())) {
                            subjectData.add(pair);
                            subjectData.add(subject);
                            subjectData.add(auditorium);
                            subjectData.add(teacher); // добавляем данные в урок
                            dayData.add(subjectData); // добавляем урок в день
                        }

                    }
                    weekData.add(dayData); // добавляем день в неделю
                }
                data.add(weekData); // добавляем неделю в общее расписание
            }
            return data;
        }
        catch (FileNotFoundException e) { // если не найден файл
            System.out.println("File not found: ".concat(filename));
        }
        catch (IOException e) {
            System.out.println("Error reading file: ".concat(filename));
        }
        catch (Throwable _) {
            return List.of();
        }
        return List.of();
    }

    private static boolean createSchedule(long groupId) {
        Map<Short, Schedule> schedules = new LinkedHashMap<>();
        List<List<List<List<Object>>>> data = readScheduleTwoColumns(groupId);

        for (short week = 0; week < 2; week++) {
            Schedule schedule = new Schedule();
            for (short dayNum = 0; dayNum < 7; dayNum++) {
                schedule.addDay(Day.values()[dayNum].getTranslation());
                for (var subjectData : data.get(week).get(dayNum)) {
                    int lessonNumber = (int) subjectData.getFirst();
                    String subject = subjectData.get(1).toString();
                    String teacher = subjectData.get(2).toString();
                    String auditorium = subjectData.get(3).toString();
                    schedule.addLesson(Day.values()[dayNum].getTranslation(), subject, lessonNumber,
                            teacher, auditorium);
                }
            }
            schedules.put(week, schedule);
        }
        try (Writer writer = new FileWriter("src/main/resources/groups/" + groupId + "/Числитель.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(schedules.get((short) 0), writer);
        }
        catch (IOException _) {
            return false;
        }
        try (Writer writer = new FileWriter("src/main/resources/groups/" + groupId + "/Знаменатель.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(schedules.get((short) 1), writer);
            return true;
        }
        catch (IOException _) {
        }
        return false;
    }

    private static boolean readLessonTime(long groupId) {
        String filename = String.format("src/main/resources/groups/%d/Schedule.xlsx", groupId);
        try(XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(filename))) {
            XSSFSheet mySheet = myExcelBook.getSheetAt(1); // вторая таблица
            XSSFRow currentRow;
            Map<Short, String> timetable = new LinkedHashMap<>();
            for (short pair = 1; pair < 11; pair++) {
                currentRow = mySheet.getRow(pair);
                String time = currentRow.getCell(1).toString();
                if (time.isEmpty())
                    time = "Время не указано";
                timetable.put(pair, time);
            }
            try (Writer writer = new FileWriter("src/main/resources/groups/" + groupId + "/Время.json")) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(timetable, writer);
            }
            catch (IOException _) {
                return false;
            }
            return true;
        }
        catch (FileNotFoundException e) { // если не найден файл
            System.out.println("File not found: ".concat(filename));
        }
        catch (IOException e) {
            System.out.println("Error reading file: ".concat(filename));
        }
        catch (Throwable _) {
            return false;
        }
        return false;
    }
}
