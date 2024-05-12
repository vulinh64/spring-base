package com.vulinh.data.repository;

import com.vulinh.data.entity.Tag;
import java.util.Collection;
import java.util.Set;

public interface TagRepository extends BaseRepository<Tag, String> {

  Set<Tag> findByDisplayNameInIgnoreCase(Collection<String> displayName);
}
