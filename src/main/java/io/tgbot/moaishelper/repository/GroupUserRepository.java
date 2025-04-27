package io.tgbot.moaishelper.repository;

import io.tgbot.moaishelper.model.groupUser.GroupUser;
import io.tgbot.moaishelper.model.groupUser.GroupUserId;
import org.springframework.data.repository.CrudRepository;

public interface GroupUserRepository extends CrudRepository<GroupUser, GroupUserId> {
}
