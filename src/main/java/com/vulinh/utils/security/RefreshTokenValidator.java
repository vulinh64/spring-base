package com.vulinh.utils.security;

import com.vulinh.data.dto.security.DecodedJwtPayload;
import org.springframework.lang.NonNull;

public interface RefreshTokenValidator {

  @NonNull
  DecodedJwtPayload validateRefreshToken(String refreshToken);
}
