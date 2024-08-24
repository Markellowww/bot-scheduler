package io.tgbot.moaishelper.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.boot.autoconfigure.graphql.ConditionalOnGraphQlSchema;

@Entity
@Getter
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String description;
}
