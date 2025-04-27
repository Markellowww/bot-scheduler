package io.tgbot.moaishelper.repository;

import io.tgbot.moaishelper.model.Groupe;
import org.springframework.data.repository.CrudRepository;

/**
 * @Authors: Markelloww & YDK
 */

public interface GroupRepository extends CrudRepository<Groupe, Long> {
    Groupe findByOwnerId(Long id);
}
