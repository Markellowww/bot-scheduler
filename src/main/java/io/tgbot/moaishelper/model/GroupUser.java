package io.tgbot.moaishelper.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * @Authors: Markelloww & YDK
 */

@Entity
@Getter
public class GroupUser {
    @EmbeddedId
    private GroupUserId id;

    @Setter
    private boolean admin;

    private AdminScheduleSettings settings;

    public GroupUser() {
    }

    public GroupUser(Groupe group, User user, boolean admin) {
        this.id = new GroupUserId(group, user);
        this.settings = new AdminScheduleSettings();
        this.admin = admin;
    }

    public Groupe getGroup() {
        return id.getGroup();
    }

    public User getUser() {
        return id.getUser();
    }

    public void setSettings(AdminScheduleSettings settings) {
        this.settings = settings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupUser that = (GroupUser) o;
        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
