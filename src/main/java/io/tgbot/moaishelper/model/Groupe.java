package io.tgbot.moaishelper.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @Authors: Markelloww & YDK
 */

@Entity
@Getter
@NoArgsConstructor
public class Groupe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<GroupUser> groupUsers = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ownerId", referencedColumnName = "id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Timestamp createdAt;

    public Groupe(User creator, String name, Timestamp createdAt) {
        this.owner = creator;
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

    public void setOwner(User owner) {
        this.owner = owner;
        setAdmin(owner);
    }

    public void setAdmin(User user) {
        for (GroupUser groupUser : groupUsers) {
            if (groupUser.getUser().getId() == user.getId()) {
                groupUser.setAdmin(true);
                groupUser.setSettings(new AdminScheduleSettings());
                return;
            }
        }
    }

    public void removeAdmin(User user) {
        for (GroupUser groupUser : groupUsers) {
            if (groupUser.getUser().getId() == user.getId()) {
                groupUser.setAdmin(false);
                groupUser.setSettings(null);
                return;
            }
        }
    }

    public List<User> getAdmins() {
        return groupUsers.stream().filter(GroupUser::isAdmin).map(GroupUser::getUser).toList();
    }

    public List<User> getMembers() {
        return groupUsers.stream().map(GroupUser::getUser).toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Groupe groupe = (Groupe) o;
        return this.id == groupe.id && this.groupUsers.equals(groupe.groupUsers) && this.name.equals(groupe.name) &&
                this.createdAt.equals(groupe.createdAt) && this.owner.equals(groupe.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, groupUsers, name, createdAt, owner);
    }
}
