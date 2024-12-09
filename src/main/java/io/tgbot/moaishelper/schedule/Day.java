package io.tgbot.moaishelper.schedule;

import lombok.Getter;

/**
 * @Authors: Markelloww & YDK
 */
@Getter
public enum Day {
    MONDAY(0, "Понедельник"),
    TUESDAY(1, "Вторник"),
    WEDNESDAY(2, "Среда"),
    THURSDAY(3, "Четверг"),
    FRIDAY(4, "Пятница"),
    SATURDAY(5, "Суббота"),
    SUNDAY(6, "Воскресенье");

    private final int order;
    private final String translation;

    Day(int order, String translation) {
        this.order = order;
        this.translation = translation;
    }
}