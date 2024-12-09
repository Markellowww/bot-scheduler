package io.tgbot.moaishelper.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * @Authors: Markelloww & YDK
 */
@Embeddable
@NoArgsConstructor
@Getter
public class GroupUserId implements Serializable {
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "groupId", referencedColumnName = "id", nullable = false)
    private Groupe group;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    GroupUserId(Groupe group, User user) {
        this.group = group;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupUserId that = (GroupUserId) o;
        return this.group.getId() == that.group.getId() && this.user.getId() == that.user.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(group.getId(), user.getId());
    }
}
