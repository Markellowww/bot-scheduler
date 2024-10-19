package io.tgbot.moaishelper.schedule;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @Authors: Markelloww & YDK
 */

public class DayWeek {

    public static short dayOfWeek(ZoneId zone) {
        return (short) (ZonedDateTime.now(zone).getDayOfWeek().getValue() - 1);
    }

    public static short weekNum(ZoneId zone) {
        ZonedDateTime today = ZonedDateTime.now(zone);
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

    public static String dayOfWeekData(short dayNum) {
        return switch (dayNum) {
            case 0 -> "MONDAY";
            case 1 -> "TUESDAY";
            case 2 -> "WEDNESDAY";
            case 3 -> "THURSDAY";
            case 4 -> "FRIDAY";
            case 5 -> "SATURDAY";
            case 6 -> "SUNDAY";
            default -> "";
        };
    }
}
