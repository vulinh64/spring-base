package com.vulinh.data.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EntityType {
  POST("Post"),
  COMMENT("Comment"),
  CATEGORY("Category"),
  POST_REVISION("Post Revision");

  final String entityName;
}
