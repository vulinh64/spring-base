package com.vulinh.utils.security;

import com.vulinh.data.dto.security.AccessTokenContainer;
import java.time.Instant;
import java.util.UUID;
import org.springframework.lang.NonNull;

public interface AccessTokenGenerator {

  @NonNull
  AccessTokenContainer generateAccessToken(UUID userId, UUID sessionId, Instant issuedAt);
}
