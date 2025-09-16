package com.vulinh.data.repository;

import module java.base;

import com.vulinh.data.base.BaseRepository;
import com.vulinh.data.entity.Comment;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends BaseRepository<Comment, UUID> {}
