package com.vulinh.data.repository;

import com.vulinh.data.base.BaseRepository;
import com.vulinh.data.entity.Tag;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface TagRepository extends BaseRepository<Tag, UUID> {

  Set<Tag> findByDisplayNameInIgnoreCase(Collection<String> displayName);
}
