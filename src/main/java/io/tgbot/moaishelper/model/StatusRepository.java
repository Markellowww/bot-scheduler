package io.tgbot.moaishelper.model;

import org.springframework.data.repository.CrudRepository;

/**
 * @Authors: Markelloww & YDK
 */

public interface StatusRepository extends CrudRepository<Status, Integer> {
    Status findById(int chatId);
}
