package com.vulinh.utils.security;

import com.vulinh.data.dto.carrier.RefreshTokenCarrier;
import com.vulinh.data.entity.ids.UserSessionId;
import java.time.Instant;
import org.springframework.lang.NonNull;

public interface RefreshTokenGenerator {

  @NonNull
  RefreshTokenCarrier generateRefreshToken(UserSessionId userSessionId, Instant issuedAt);
}
