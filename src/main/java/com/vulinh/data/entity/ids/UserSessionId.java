package com.vulinh.data.entity.ids;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public record UserSessionId(UUID userId, UUID sessionId) implements Serializable {

  public static UserSessionId of(UUID userId, UUID sessionId) {
    return new UserSessionId(userId, sessionId);
  }
}
