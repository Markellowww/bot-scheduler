package io.tgbot.moaishelper.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

import java.util.Date;

@Entity
@Getter
public class GroupFile {
    @Id
    private Long id;

    @ManyToOne
    private Groupe group;

    @ManyToOne
    private User sender;

    @Column
    private String fileName;

    @Column
    private Date createdAt;

    @Column
    private int daysToStore;

    public GroupFile() {}

    public GroupFile(Groupe group, User sender, String fileName, int daysToStore) {
        this.group = group;
        this.sender = sender;
        this.fileName = fileName;
        this.daysToStore = daysToStore;
        this.createdAt = new Date();
    }

    public void setGroup(Groupe group) {
        this.group = group;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setDaysToStore(int daysToStore) {
        this.daysToStore = daysToStore;
    }
}
