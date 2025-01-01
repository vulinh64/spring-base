package com.vulinh.data.repository;

import com.vulinh.data.base.BaseRepository;
import com.vulinh.data.entity.CommentRevision;
import com.vulinh.data.entity.ids.CommentRevisionId;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRevisionRepository
    extends BaseRepository<CommentRevision, CommentRevisionId> {}
