package io.tgbot.moaishelper.repository;

import io.tgbot.moaishelper.model.Status;
import org.springframework.data.repository.CrudRepository;

/**
 * @Authors: Markelloww & YDK
 */
public interface StatusRepository extends CrudRepository<Status, Integer> {
    Status findById(int chatId);
}
