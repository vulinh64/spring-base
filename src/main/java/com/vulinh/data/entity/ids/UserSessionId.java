package com.vulinh.data.entity.ids;

import module java.base;

import jakarta.persistence.Embeddable;

@Embeddable
public record UserSessionId(UUID userId, UUID sessionId) implements Serializable {

  public static UserSessionId of(UUID userId, UUID sessionId) {
    return new UserSessionId(userId, sessionId);
  }
}
