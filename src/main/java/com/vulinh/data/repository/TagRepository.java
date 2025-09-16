package com.vulinh.data.repository;

import module java.base;

import com.vulinh.data.base.BaseRepository;
import com.vulinh.data.entity.Tag;

public interface TagRepository extends BaseRepository<Tag, UUID> {

  Set<Tag> findByDisplayNameInIgnoreCase(Collection<String> displayName);
}
