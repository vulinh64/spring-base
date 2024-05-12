package com.vulinh.data.projection;

import com.vulinh.utils.StringIdentifiable;

public interface AuthorProjection extends StringIdentifiable {

  String getUsername();

  String getFullName();
}
