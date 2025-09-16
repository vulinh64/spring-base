package com.vulinh.data.dto.projection;

import module java.base;

import com.vulinh.data.base.DateTimeAuditable;
import com.vulinh.data.base.UUIDIdentifiable;

public interface PrefetchPostProjection extends UUIDIdentifiable, DateTimeAuditable {

  String getTitle();

  String getExcerpt();

  String getSlug();

  AuthorProjection getAuthor();

  CategoryProjection getCategory();

  @Override
  Instant getCreatedDate();

  @Override
  Instant getUpdatedDate();
}
