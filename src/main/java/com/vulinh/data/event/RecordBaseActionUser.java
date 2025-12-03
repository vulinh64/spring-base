package com.vulinh.data.event;

import java.util.UUID;

public interface RecordBaseActionUser extends BaseActionUser {

  UUID id();

  String username();

  @Override
  default String getUsername() {
    return username();
  }

  @Override
  default UUID getId() {
    return id();
  }
}
