package io.tgbot.moaishelper.model.groupUser;

import io.tgbot.moaishelper.model.AdminScheduleSettings;
import io.tgbot.moaishelper.model.Groupe;
import io.tgbot.moaishelper.model.User;
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
    @EmbeddedId
    private GroupUserId groupUserId;

    @Getter
    @MapsId("groupId")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private Groupe group;

    @Getter
    @MapsId("userId")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private User user;

    @Setter
    private boolean admin;

    @Setter
    @Column(name = "settings", length = 384)
    private AdminScheduleSettings settings;

    public GroupUser() {
    }

    public GroupUser(Groupe group, User user, boolean admin) {
        this.groupUserId = new GroupUserId(group.getId(), user.getId());
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
