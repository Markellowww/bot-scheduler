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
    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "groupID", referencedColumnName = "id", nullable = false)
    @Id
    private Groupe group;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "userID", referencedColumnName = "id", nullable = false)
    @Id
    private User user;

    @Setter
    private boolean admin;

    @OneToOne(mappedBy = "groupUser", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private AdminScheduleSettings settings;

    public GroupUser() {
    }

    public GroupUser(Groupe group, User user, boolean admin) {
        this.group = group;
        this.user = user;
        this.admin = admin;
    }

    public void setSettings(AdminScheduleSettings settings) {
        this.settings = settings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupUser that = (GroupUser) o;
        return this.group.getId() == that.group.getId() && this.user.getId() == that.user.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(group.getId(), user.getId());
    }
}
