package io.tgbot.moaishelper.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.tgbot.moaishelper.schedule.Schedule;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * @Authors: Markelloww & YDK
 */

public class ExcelParser {
    public static void main(String[] args) throws IOException {
        createSchedule(1, readExcel("src/main/resources/groups/1/Schedule.xlsx")); // тут нужен нормальный путь
    }

    public static List<List<List<List<Object>>>> readExcel(String fileName) {
        try(XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(fileName))) {
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

                        if (Stream.of(subject, teacher, auditorium).anyMatch(s -> !s.isEmpty())) {
                            subjectData.add(pair);
                            subjectData.add(subject);
                            subjectData.add(teacher); // добавляем данные в урок
                            subjectData.add(auditorium);
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
            System.out.println("File not found: " + fileName);
        }
        catch (IOException e) {
            System.out.println("Error reading file: " + fileName);
        }
        return List.of();
    }

    public static void createSchedule(long groupId, List<List<List<List<Object>>>> data) throws IOException {
        Map<Short, Schedule> schedules = new LinkedHashMap<>();

        for (short week = 0; week < 2; week++) {
            Schedule schedule = new Schedule();
            for (short dayNum = 0; dayNum < 6; dayNum++) {
                schedule.addDay(dayOfWeek(dayNum));
                System.out.println(data.get(week).get(dayNum));
                for (var subjectData : data.get(week).get(dayNum)) {
                    short pair = (short) subjectData.getFirst();
                    String subject = subjectData.get(1).toString();
                    String teacher = subjectData.get(2).toString();
                    String auditorium = subjectData.get(3).toString();
                    schedule.addLesson(dayOfWeek(dayNum), subject, lessonTime(pair), teacher, auditorium);
                }
            }
            schedules.put(week, schedule);
            System.out.println();
        }

        try (Writer writer = new FileWriter("src/main/resources/groups/" + groupId + "/Schedule.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(schedules, writer);
        }
    }

    private static String dayOfWeek(short dayNum) {
        return switch (dayNum) {
            case 0 -> "Понедельник";
            case 1 -> "Вторник";
            case 2 -> "Среда";
            case 3 -> "Четверг";
            case 4 -> "Пятница";
            case 5 -> "Суббота";
            default -> "";
        };
    }

    public static String lessonTime(short lessonNum) {
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
