package com.vulinh.utils.security;

import module java.base;

import com.vulinh.data.dto.carrier.RefreshTokenCarrier;
import com.vulinh.data.entity.ids.UserSessionId;
import org.springframework.lang.NonNull;

public interface RefreshTokenGenerator {

  @NonNull
  RefreshTokenCarrier generateRefreshToken(UserSessionId userSessionId, Instant issuedAt);
}
