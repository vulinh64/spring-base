package com.vulinh.factory;

import com.vulinh.data.entity.PostRevisionId;
import com.vulinh.data.entity.Tag;

@SuppressWarnings("java:S6548")
public enum PostFactory {
  INSTANCE;

  public PostRevisionId createRevisionId(String postId, Long revisionNumber) {
    return new PostRevisionId(postId, revisionNumber);
  }

  public Tag createTag(String displayName) {
    return Tag.builder().displayName(displayName).build();
  }
}
