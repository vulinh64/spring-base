package com.vulinh.data.projection;

import com.vulinh.utils.StringIdentifiable;
import java.time.LocalDateTime;

public interface PrefetchPostProjection extends StringIdentifiable {

  String getTitle();

  String getExcerpt();

  String getSlug();

  LocalDateTime getCreatedDate();

  LocalDateTime getUpdatedDate();

  AuthorProjection getAuthor();

  CategoryProjection getCategory();
}
