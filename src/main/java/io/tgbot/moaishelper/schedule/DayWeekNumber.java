package io.tgbot.moaishelper.schedule;

import java.time.DayOfWeek;
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

        // Опорная дата: понедельник, 1 сентября 2025
        LocalDate baseMonday = LocalDate.of(2025, 9, 1);

        // Текущий понедельник недели
        LocalDate currentWeekMonday = today.toLocalDate().with(DayOfWeek.MONDAY);

        // Количество недель между базовым понедельником и текущим понедельником
        long weeksBetween = ChronoUnit.WEEKS.between(baseMonday, currentWeekMonday);

        // Возвращаем 0 или 1 в зависимости от четности недели
        return (short) (weeksBetween % 2);
    }
}
