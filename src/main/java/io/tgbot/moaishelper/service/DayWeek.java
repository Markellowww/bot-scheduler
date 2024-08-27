package io.tgbot.moaishelper.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DayWeek {
    public static void main(String[] args) {
        System.out.println(dayOfWeek());
        System.out.println(weekNum());
    }

    public static int dayOfWeek() {
        return LocalDate.now().getDayOfWeek().getValue() - 1;
    }

    public static int weekNum() {
        LocalDate today = LocalDate.now();
        LocalDate september2;
        if (today.getMonthValue() >= 9)
            september2 = LocalDate.of(today.getYear(), 9, 2);
        else
            september2 = LocalDate.of(today.getYear() - 1, 9, 2);
        return (int) september2.until(today, ChronoUnit.DAYS) / 7 % 2;
    }
}
