package io.tgbot.moaishelper.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class AdminScheduleSettings {
    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @Id
    private GroupUser groupUser;

    @Column
    private Boolean weekNum;

    @Column
    private Short weekDay;

    @Column
    private Short lessonNum;

    public AdminScheduleSettings() {
        lessonNum = null;
        weekNum = null;
        weekDay = null;
    }

    public void setWeekNum(Boolean weekNum) {
        this.weekNum = weekNum;
    }

    public void setGroupUser(GroupUser groupUser) {
        this.groupUser = groupUser;
    }

    public void setWeekDay(Short weekDay) {
        this.weekDay = weekDay;
    }

    public void setLessonNum(Short lessonNum) {
        this.lessonNum = lessonNum;
    }
}
