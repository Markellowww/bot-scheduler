package io.tgbot.moaishelper.repository;

import io.tgbot.moaishelper.model.UserSettings;
import org.springframework.data.repository.CrudRepository;

/**
 * @Authors: YDKrivoshey
 */
public interface UserSettingsRepository extends CrudRepository<UserSettings, Long> {
    UserSettings findById(long id);
}
