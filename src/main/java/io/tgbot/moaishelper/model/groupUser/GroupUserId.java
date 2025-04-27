package io.tgbot.moaishelper.model.groupUser;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

/**
 * @Authors: Markelloww & YDK
 */
@Embeddable
@Getter
public class GroupUserId implements Serializable {
    @Column
    private Long groupId;

    @Column
    private Long userId;

    public GroupUserId(Long groupId, Long userId) {
        this.groupId = groupId;
        this.userId = userId;
    }

    public GroupUserId() {}

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GroupUserId that)) return false;
        return Objects.equals(groupId, that.groupId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, userId);
    }
}
