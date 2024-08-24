package io.tgbot.moaishelper.model;

import org.springframework.data.repository.CrudRepository;

public interface StatusRepository extends CrudRepository<Status, Integer> {
    Status findById(int chatId);
}
