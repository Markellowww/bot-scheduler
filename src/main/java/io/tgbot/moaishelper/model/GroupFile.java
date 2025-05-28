package io.tgbot.moaishelper.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

@Entity
public class GroupFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Groupe group;

    @ManyToOne(fetch = FetchType.EAGER)
    private User sender;

    @Column
    private String fileName;

    @Column
    private LocalDate createdAt;

    @Column
    private int daysToStore;

    public GroupFile() {}

    public GroupFile(Groupe group, User sender, String fileName, int daysToStore) {
        this.group = group;
        this.sender = sender;
        this.fileName = fileName;
        this.daysToStore = daysToStore;
        this.createdAt = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public Groupe getGroup() {
        return group;
    }

    public User getSender() {
        return sender;
    }

    public String getFileName() {
        return fileName;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public int getDaysToStore() {
        return daysToStore;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GroupFile groupFile)) return false;
        return id == groupFile.id && daysToStore == groupFile.daysToStore && Objects.equals(group, groupFile.group) && Objects.equals(sender, groupFile.sender) && Objects.equals(fileName, groupFile.fileName) && Objects.equals(createdAt, groupFile.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, group, sender, fileName, createdAt, daysToStore);
    }
}
