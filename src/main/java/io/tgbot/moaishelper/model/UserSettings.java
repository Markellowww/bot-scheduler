package io.tgbot.moaishelper.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
public class UserSettings {
    @Id
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private User user;

    @Column(nullable = false)
    private String timeZoneId;

    @Column(nullable = false)
    private boolean notificationsEnabled;

    @Column(nullable = false)
    private short notificationHours;

    @Column(nullable = false)
    private short notificationMinutes;

    public UserSettings() {
        this.timeZoneId = "Europe/Moscow";
        this.notificationsEnabled = true;
        this.notificationHours = 12;
        this.notificationMinutes = 0;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public void setNotificationHours(short notificationHours) {
        this.notificationHours = notificationHours;
    }

    public void setNotificationMinutes(short notificationMinutes) {
        this.notificationMinutes = notificationMinutes;
    }

    public void setUser(User user) {
        this.user = user;
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
