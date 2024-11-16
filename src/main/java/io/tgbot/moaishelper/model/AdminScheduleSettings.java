package io.tgbot.moaishelper.model;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AdminScheduleSettings implements Serializable {
    @Column
    private Boolean weekNum;

    @Column
    private Short weekDay;

    @Column
    private Short lessonNum;

    public AdminScheduleSettings() {
        this.weekNum = true;
        this.weekDay = null;
        this.lessonNum = null;
    }
}