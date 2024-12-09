package io.tgbot.moaishelper.model;

import jakarta.persistence.*;
import lombok.Getter;

/**
 * @Authors: Markelloww & YDK
 */
@Entity
@Getter
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status = (Status) o;
        return id == status.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
