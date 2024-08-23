package io.tgbot.moaishelper.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @Authors: Markelloww & YDK
 */

public class ExcelParser {
    public static void main(String[] args) throws IOException {
        createSchedule(1, readExcel("src/main/resources/groups/1/Schedule.xlsx")); // тут нужен нормальный путь
    }

    public static List<List<Object>> readExcel(String fileName) {
        try(XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(fileName))) {
            XSSFSheet mySheet = myExcelBook.getSheetAt(0); // первая таблица
            XSSFRow currentRow; // текущая строка из excel

            List<List<Object>> data = new ArrayList<>(2);
            short rowIndex = 3; // текущая строка по индексу (см шаблон)
            for (short day = 1; day < 7; day++, rowIndex += 2) { // обход по дням
                for (short pair = 1; pair < 8; pair++, rowIndex++) { // обход пар в дне
                    currentRow = mySheet.getRow(rowIndex); // получаем текущий столбец
                    short cellIndex = 1; // индекс клетки в строке
                    for (short week = 0; week < 2; week++) { // обход по неделям (числитель -> знаменатель)
                        String subject = currentRow.getCell(cellIndex++).toString();
                        String teacher = currentRow.getCell(cellIndex++).toString();
                        String auditorium  = currentRow.getCell(cellIndex++).toString();

//                        System.out.println(subject + " " + teacher + " " + auditorium);
                        if (!Stream.of(subject, teacher, auditorium).allMatch(String::isEmpty)) {
                            System.out.println(subject + " " + teacher + " " + auditorium);
                            /* добавляем в json данные
                            [четность недели -> [номер дня -> [номер пары, предмет, преподаватель, аудитория]]]*/
                        }
                        cellIndex += 2; // смещаемся к полям знаменателя
                    }
                }
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

    public static void createSchedule(long groupId, List<List<Object>> data) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(Paths.get("src/main/resources/groups/" + groupId + "/Schedule.json").toFile(),
                    data);
        }
        catch (IOException e) {
            System.out.println("Error creating Schedule: " + e.getMessage());
        }
    }
}
