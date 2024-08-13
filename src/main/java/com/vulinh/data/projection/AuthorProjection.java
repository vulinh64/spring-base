package com.vulinh.data.projection;

import com.vulinh.utils.UUIDIdentifiable;

public interface AuthorProjection extends UUIDIdentifiable {

  String getUsername();

  String getFullName();
}
