package com.vulinh.data.repository;

import com.vulinh.data.entity.CommentRevision;
import com.vulinh.data.entity.ids.CommentRevisionId;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRevisionRepository
    extends org.springframework.data.jpa.repository.JpaRepository<CommentRevision, CommentRevisionId>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<CommentRevision>, org.springframework.data.querydsl.ListQuerydslPredicateExecutor<CommentRevision>, org.springframework.data.repository.query.QueryByExampleExecutor<CommentRevision> {}
