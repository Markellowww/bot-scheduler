package io.tgbot.moaishelper.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
public class UserSettings {
    @Id
    private long id;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private User user;

    @Setter
    @Column(nullable = false)
    private String timeZoneId;

    @Setter
    @Column(nullable = false)
    private boolean notificationsEnabled;

    @Setter
    @Column(nullable = false)
    private short notificationHours;

    @Setter
    @Column(nullable = false)
    private short notificationMinutes;

    public UserSettings() {
        this.timeZoneId = "Europe/Moscow";
        this.notificationsEnabled = true;
        this.notificationHours = 12;
        this.notificationMinutes = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSettings that = (UserSettings) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
