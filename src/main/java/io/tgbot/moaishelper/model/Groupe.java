package io.tgbot.moaishelper.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: YDKrivoshey
 */

@Entity
@Getter
@NoArgsConstructor
public class Groupe { // французское слово, group[s] - ключевые в SQL!!!
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "group")
    private List<GroupUser> groupUsers = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creatorID", referencedColumnName = "id", nullable = false)
    private User creator;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Timestamp createdAt;

    public Groupe(User creator, String name, Timestamp createdAt) {
        this.creator = creator;
        this.name = name;
        this.createdAt = createdAt;
        addMember(creator, true);
    }

    public void addMember(User user, boolean admin) {
        GroupUser groupUser = new GroupUser(this, user, admin);
        groupUsers.add(groupUser);
    }

    public void removeMember(User user) {
        for (Iterator<GroupUser> it = groupUsers.iterator(); it.hasNext(); ) {
            GroupUser groupUser = it.next();
            if (groupUser.getGroup().getId() == this.id && groupUser.getUser().getId() == user.getId()) {
                it.remove();
                return;
            }
        }
    }
}
