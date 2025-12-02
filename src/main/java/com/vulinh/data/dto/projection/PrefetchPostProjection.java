package com.vulinh.data.dto.projection;

import module java.base;

import com.vulinh.data.base.DateTimeAuditable;
import com.vulinh.data.base.UuidIdentifiable;

public interface PrefetchPostProjection extends UuidIdentifiable, DateTimeAuditable {

  String getTitle();

  String getExcerpt();

  String getSlug();

  UUID getAuthorId();

  CategoryProjection getCategory();

  @Override
  Instant getCreatedDate();

  @Override
  Instant getUpdatedDate();
}
