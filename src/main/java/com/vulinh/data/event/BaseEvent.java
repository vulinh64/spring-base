package com.vulinh.data.event;

import module java.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vulinh.data.base.UuidIdentifiable;

public interface BaseEvent extends UuidIdentifiable {

  default UUID getEventId() {
    return UUID.randomUUID();
  }

  default Instant getTimestamp() {
    return Instant.now();
  }

  // eventId is enough
  @JsonIgnore
  @Override
  default UUID getId() {
    return getEventId();
  }
}
