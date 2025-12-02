package com.vulinh.data.event;

import module java.base;

import lombok.Builder;
import lombok.With;

@Builder
@With
public record NewPostEvent(
    UUID eventId, UUID postId, UUID authorId, String username, String title, Instant timestamp)
    implements BaseEvent {

  public NewPostEvent {
    // Override the value regardless of the input
    eventId = UUID.randomUUID();
    timestamp = Instant.now();
  }
}
