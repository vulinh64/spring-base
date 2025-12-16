package com.vulinh.data.dto.projection;

import module java.base;

import com.vulinh.data.base.Identifiable;
import com.vulinh.data.base.InstantDateTimeAuditable;

public interface PrefetchPostProjection extends InstantDateTimeAuditable, Identifiable<UUID> {

  String getTitle();

  String getExcerpt();

  String getSlug();

  UUID getAuthorId();

  CategoryProjection getCategory();

  @Override
  Instant getCreatedDateTime();

  @Override
  Instant getUpdatedDateTime();
}
