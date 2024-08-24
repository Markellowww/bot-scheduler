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
}
