package com.vulinh.data.repository;

import com.vulinh.data.entity.CommentRevision;
import com.vulinh.data.entity.CommentRevisionId;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRevisionRepository
    extends BaseRepository<CommentRevision, CommentRevisionId> {}
