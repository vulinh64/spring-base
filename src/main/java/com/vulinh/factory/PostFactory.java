package com.vulinh.factory;

import module java.base;

import com.vulinh.data.entity.Tag;
import com.vulinh.data.entity.ids.PostRevisionId;

@SuppressWarnings("java:S6548")
public enum PostFactory {
  INSTANCE;

  public PostRevisionId createRevisionId(UUID postId, Long revisionNumber) {
    return new PostRevisionId(postId, revisionNumber);
  }

  public Tag createTag(String displayName) {
    return Tag.builder().displayName(displayName).build();
  }
}
