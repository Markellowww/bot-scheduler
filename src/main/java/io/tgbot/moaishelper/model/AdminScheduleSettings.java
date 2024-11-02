package io.tgbot.moaishelper.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class AdminScheduleSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @OneToOne
    @MapsId
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
