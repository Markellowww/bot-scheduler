package io.tgbot.moaishelper.model;

import org.springframework.data.repository.CrudRepository;

/**
 * @Authors: Markelloww & YDK
 */
public interface UserRepository extends CrudRepository<User, Long> {
    User findByChatId(Long chatId);
    User findByUserName(String userName);
}
