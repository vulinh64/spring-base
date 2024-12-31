package com.vulinh.utils.security;

import com.vulinh.data.dto.security.RefreshTokenContainer;
import com.vulinh.data.entity.ids.UserSessionId;
import java.time.Instant;
import org.springframework.lang.NonNull;

public interface RefreshTokenGenerator {

  @NonNull
  RefreshTokenContainer generateRefreshToken(UserSessionId userSessionId, Instant issuedAt);
}
