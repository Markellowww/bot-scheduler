package io.tgbot.moaishelper.model;

import org.springframework.data.repository.CrudRepository;

/**
 * @Author: YDKrivoshey
 */

public interface GroupRepository extends CrudRepository<Groupe, Long> {
    Groupe findByCreatorId(long creatorId);
}
