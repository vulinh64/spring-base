package com.vulinh.data.event;

import module java.base;

import com.vulinh.data.base.RecordUuidIdentifiable;

public interface BaseEvent extends RecordUuidIdentifiable {

  UUID eventId();

  Instant timestamp();

  @Override
  default UUID id() {
    return eventId();
  }
}
