package com.vulinh.data.projection;

import com.vulinh.data.base.DateTimeAuditable;
import com.vulinh.utils.UUIDIdentifiable;

public interface PrefetchPostProjection extends UUIDIdentifiable, DateTimeAuditable {

  String getTitle();

  String getExcerpt();

  String getSlug();

  AuthorProjection getAuthor();

  CategoryProjection getCategory();
}
