package com.vulinh.data.event;

import module java.base;

import com.vulinh.data.base.UuidIdentifiable;

public interface BaseActionUser extends UuidIdentifiable {

  @Override
  UUID getId();

  String getUsername();
}
