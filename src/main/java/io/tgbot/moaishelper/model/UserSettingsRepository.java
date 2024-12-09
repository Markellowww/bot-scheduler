package io.tgbot.moaishelper.model;

import org.springframework.data.repository.CrudRepository;

/**
 * @Authors: YDKrivoshey
 */
public interface UserSettingsRepository extends CrudRepository<UserSettings, Long> {
    UserSettings findById(long id);
}
