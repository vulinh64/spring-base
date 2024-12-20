package com.vulinh.data.projection;

import com.vulinh.data.base.UUIDIdentifiable;

public interface AuthorProjection extends UUIDIdentifiable {

  String getUsername();

  String getFullName();
}
