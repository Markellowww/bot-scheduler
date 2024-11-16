package io.tgbot.moaishelper.schedule;

import lombok.Getter;

@Getter
public enum Day {
    MONDAY(1, "Понедельник"),
    TUESDAY(2, "Вторник"),
    WEDNESDAY(3, "Среда"),
    THURSDAY(4, "Четверг"),
    FRIDAY(5, "Пятница"),
    SATURDAY(6, "Суббота"),
    SUNDAY(7, "Воскресенье");

    private final int order;
    private final String translation;

    Day(int order, String translation) {
        this.order = order;
        this.translation = translation;
    }
}