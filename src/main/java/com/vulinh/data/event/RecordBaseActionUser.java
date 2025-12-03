package com.vulinh.data.event;

import module java.base;

public interface RecordBaseActionUser extends BaseActionUser {

  UUID id();

  String username();

  @Override
  default UUID getId() {
    return id();
  }

  @Override
  default String getUsername() {
    return username();
  }
}
