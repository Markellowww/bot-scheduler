package io.tgbot.moaishelper.model;

import org.springframework.data.repository.CrudRepository;

/**
 * @Authors: Markelloww & YDK
 */

public interface GroupRepository extends CrudRepository<Groupe, Long> {
    Groupe findByCreatorId(long creatorId);
}
