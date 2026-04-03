package com.vulinh.data.dto.projection;

import module java.base;

public interface PostSearchProjection {

  UUID getId();

  String getTitle();

  String getExcerpt();

  String getSlug();

  Instant getCreatedDateTime();

  Instant getUpdatedDateTime();

  UUID getAuthorId();

  UUID getCategoryId();

  String getCategorySlug();

  String getDisplayName();
}
