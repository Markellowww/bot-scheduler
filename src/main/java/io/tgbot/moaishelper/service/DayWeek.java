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
        LocalDate september1;
        if (today.getMonthValue() >= 9)
            september1 = LocalDate.of(today.getYear(), 9, 1);
        else
            september1 = LocalDate.of(today.getYear() - 1, 9, 1);
        return (int) september1.until(today, ChronoUnit.DAYS) / 7 % 2;
    }
}
