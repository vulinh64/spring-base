package com.vulinh.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NamedQueryConstant {

  public static final String FIND_PREFETCHED_POSTS = "find-prefetched-posts";
  public static final String FIND_POST_REVISIONS = "find-post-revisions-by-post-id";
}
