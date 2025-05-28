package io.tgbot.moaishelper.repository;

import io.tgbot.moaishelper.model.GroupFile;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GroupFileRepository extends CrudRepository<GroupFile, Long> {
    public List<GroupFile> findByGroupId(Long groupId);
}
