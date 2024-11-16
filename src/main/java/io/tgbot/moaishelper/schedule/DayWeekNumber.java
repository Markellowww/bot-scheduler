package io.tgbot.moaishelper.schedule;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @Authors: Markelloww & YDK
 */
public class DayWeekNumber {

    public static short getDayOfWeekNumber(ZoneId zone) {
        return (short) (ZonedDateTime.now(zone).getDayOfWeek().getValue() - 1);
    }

    public static short getCurrentWeekNumber(ZoneId zone) {
        ZonedDateTime today = ZonedDateTime.now(zone);

        LocalDate september2 = today.getMonthValue() >= 9 ? LocalDate.of(today.getYear(), 9, 2) :
                LocalDate.of(today.getYear() - 1, 9, 2);

        return (short) (september2.until(today, ChronoUnit.DAYS) / 7 % 2);
    }
}
