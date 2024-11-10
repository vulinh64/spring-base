package com.vulinh.factory;

import com.vulinh.data.entity.Comment;
import com.vulinh.data.entity.CommentRevisionId;

@SuppressWarnings("java:S6548")
public enum CommentFactory {
  INSTANCE;

  public CommentRevisionId createTransientId(Comment comment) {
    return CommentRevisionId.builder().commentId(comment.getId()).build();
  }
}
