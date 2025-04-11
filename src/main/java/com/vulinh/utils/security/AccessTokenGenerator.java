package com.vulinh.utils.security;

import com.vulinh.data.dto.carrier.AccessTokenCarrier;
import java.time.Instant;
import java.util.UUID;
import org.springframework.lang.NonNull;

public interface AccessTokenGenerator {

  @NonNull
  AccessTokenCarrier generateAccessToken(UUID userId, UUID sessionId, Instant issuedAt);
}
