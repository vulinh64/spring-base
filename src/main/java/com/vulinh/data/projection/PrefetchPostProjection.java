package com.vulinh.data.projection;

import com.vulinh.utils.UUIDIdentifiable;
import java.time.LocalDateTime;

public interface PrefetchPostProjection extends UUIDIdentifiable {

  String getTitle();

  String getExcerpt();

  String getSlug();

  LocalDateTime getCreatedDate();

  LocalDateTime getUpdatedDate();

  AuthorProjection getAuthor();

  CategoryProjection getCategory();
}
