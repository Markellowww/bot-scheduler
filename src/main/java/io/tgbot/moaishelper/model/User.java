package io.tgbot.moaishelper.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Authors: Markelloww & YDK
 */

@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<GroupUser> groupUsers = new ArrayList<>();

    @Column(unique = true, nullable = false)
    private long chatId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "selectedGroupId", referencedColumnName = "id")
    private Groupe selectedGroup;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "statusId", referencedColumnName = "id", nullable = false)
    private Status status;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private UserSettings settings;

    @Column(nullable = false)
    private String firstName;

    @Column(unique = true, nullable = false)
    private String userName;

    @Column(nullable = false)
    private Timestamp registeredAt;

    public User(long chatId, String firstName, String userName, Timestamp registeredAt) {
        this.chatId = chatId;
        this.firstName = firstName;
        this.userName = userName;
        this.registeredAt = registeredAt;
        this.settings = new UserSettings();
        this.settings.setUser(this);
    }

    @Override
    public String toString() {
        return getUserName();
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSelectedGroup(Groupe selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    public void setStatus(Status status) {this.status = status;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return this.id == user.id && this.chatId == user.chatId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId);
    }
}
