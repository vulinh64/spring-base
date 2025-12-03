package com.vulinh.data.event;

import module java.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vulinh.data.base.UuidIdentifiable;

public interface BaseEvent extends UuidIdentifiable {

  @JsonProperty("eventId")
  default UUID eventId() {
    return UUID.randomUUID();
  }

  @JsonProperty("timestamp")
  default Instant timestamp() {
    return Instant.now();
  }

  // No need for @JsonProperty, the implementations will need to provide this
  UUID actionUserId();

  // No need for @JsonProperty, the implementations will need to provide this
  String actionUsername();

  // eventId is enough
  @JsonIgnore
  @Override
  default UUID getId() {
    return eventId();
  }
}
