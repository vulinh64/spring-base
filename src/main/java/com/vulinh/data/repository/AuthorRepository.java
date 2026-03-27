package com.vulinh.data.repository;

import module java.base;

import com.vulinh.data.entity.Author;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends BaseRepository<Author, UUID> {

  List<Author> findAllByIdIn(Collection<UUID> ids);
}
