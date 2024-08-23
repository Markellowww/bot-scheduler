package io.tgbot.moaishelper.model;

import jakarta.persistence.*;
import lombok.Getter;

/**
 * @Authors: Markelloww & YDK
 */

@Entity
@Getter
public class GroupUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "groupID", referencedColumnName = "id", nullable = false)
    private Groupe group;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "userID", referencedColumnName = "id", nullable = false)
    private User user;

    private boolean admin;

    public GroupUser() {
    }

    public GroupUser(Groupe group, User user, boolean admin) {
        this.group = group;
        this.user = user;
        this.admin = admin;
    }
}
