package com.vulinh.utils.security;

import module java.base;

import com.vulinh.data.dto.carrier.AccessTokenCarrier;
import org.springframework.lang.NonNull;

public interface AccessTokenGenerator {

  @NonNull
  AccessTokenCarrier generateAccessToken(UUID userId, UUID sessionId, Instant issuedAt);
}
