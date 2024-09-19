package io.tgbot.moaishelper.schedule;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * @Authors: Markelloww & YDK
 */

public class DayWeek {

    public static short dayOfWeek() {
        return (short) (LocalDate.now().getDayOfWeek().getValue() - 1);
    }

    public static short weekNum() {
        LocalDate today = LocalDate.now();
        LocalDate september2;
        if (today.getMonthValue() >= 9)
            september2 = LocalDate.of(today.getYear(), 9, 2);
        else
            september2 = LocalDate.of(today.getYear() - 1, 9, 2);
        return (short) (september2.until(today, ChronoUnit.DAYS) / 7 % 2);
    }

    public static String dayOfWeek(short dayNum) {
        return switch (dayNum) {
            case 0 -> "Понедельник";
            case 1 -> "Вторник";
            case 2 -> "Среда";
            case 3 -> "Четверг";
            case 4 -> "Пятница";
            case 5 -> "Суббота";
            case 6 -> "Воскресенье";
            default -> "";
        };
    }
}
