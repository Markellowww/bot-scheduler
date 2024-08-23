package io.tgbot.moaishelper.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Markelloww, YDKrivoshey
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

    private String firstName;

    @Column(unique = true, nullable = false) // уникально в Telegram
    private String userName;

    private Timestamp registeredAt;

    public User(long chatId, String firstName, String userName, Timestamp registeredAt) {
        this.chatId = chatId;
        this.firstName = firstName;
        this.userName = userName;
        this.registeredAt = registeredAt;
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
}
