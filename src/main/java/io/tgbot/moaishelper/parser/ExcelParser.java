package io.tgbot.moaishelper.parser;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.tgbot.moaishelper.schedule.Schedule;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

import static io.tgbot.moaishelper.schedule.DayWeek.dayOfWeek;

/**
 * @Authors: Markelloww & YDK
 */

public class ExcelParser {

    private static List<List<List<List<Object>>>> readSchedule(long groupId) {
        String filename = String.format("src/main/resources/groups/%d/Schedule.xlsx", groupId);
        try(XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(filename))) {
            XSSFSheet mySheet = myExcelBook.getSheetAt(0); // первая таблица
            XSSFRow currentRow; // текущая строка из excel

            List<List<List<List<Object>>>> data = new ArrayList<>();
            short cellIndex = 1; // индекс клетки в строке
            for (short week = 0; week < 2; week++, cellIndex += 2) {
                short rowIndex = 3; // текущая строка по индексу (см шаблон)
                List<List<List<Object>>> weekData = new ArrayList<>();

                for (short day = 1; day < 7; day++, rowIndex += 2) {
                    List<List<Object>> dayData = new ArrayList<>();

                    for (short pair = 1; pair < 8; pair++, rowIndex++) {
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
        return List.of();
    }

    public static void createSchedule(long groupId) {
        Map<Short, Schedule> schedules = new LinkedHashMap<>();
        List<List<List<List<Object>>>> data = readSchedule(groupId);

        for (short week = 0; week < 2; week++) {
            Schedule schedule = new Schedule();
            for (short dayNum = 0; dayNum < 6; dayNum++) {
                schedule.addDay(dayOfWeek(dayNum));
                for (var subjectData : data.get(week).get(dayNum)) {
                    short pair = (short) subjectData.getFirst();
                    String subject = subjectData.get(1).toString();
                    String teacher = subjectData.get(2).toString();
                    String auditorium = subjectData.get(3).toString();
                    schedule.addLesson(dayOfWeek(dayNum), subject, lessonTime(pair), teacher, auditorium);
                }
            }
            schedules.put(week, schedule);
        }

        try (Writer writer = new FileWriter("src/main/resources/groups/" + groupId + "/Числитель.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(schedules.get((short) 0), writer);
        }
        catch (IOException _) {
        }
        try (Writer writer = new FileWriter("src/main/resources/groups/" + groupId + "/Знаменатель.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(schedules.get((short) 1), writer);
        }
        catch (IOException _) {
        }
    }

    public static String lessonTime(short lessonNum) { // временный выход
        return switch (lessonNum) {
            case 1 -> "8:00 – 9:20";
            case 2 -> "9:30 – 10:50";
            case 3 -> "11.10 – 12:30";
            case 4 -> "12:40 – 14:00";
            case 5 -> "14:10 – 15:30";
            case 6 -> "15:40 – 17:00";
            case 7 -> "17:10 – 18:30";
            case 8 -> "18:40 – 20:00";
            default -> "";
        };
    }
}
