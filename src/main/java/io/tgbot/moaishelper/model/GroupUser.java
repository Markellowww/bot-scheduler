package io.tgbot.moaishelper.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @Authors: Markelloww & YDK
 */

@Entity
@Getter
public class GroupUser {
    @Getter
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "groupId", referencedColumnName = "id", nullable = false)
    @Id
    private Groupe group;

    @Getter
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    @Id
    private User user;

    @Setter
    private boolean admin;

    @Setter
    @Column(name = "settings", length = 500)
    private AdminScheduleSettings settings;

    public GroupUser() {
    }

    public GroupUser(Groupe group, User user, boolean admin) {
        this.group = group;
        this.user = user;
        this.settings = new AdminScheduleSettings();
        this.admin = admin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupUser that = (GroupUser) o;
        return this.group.equals(that.group) && this.user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, user);
    }
}
