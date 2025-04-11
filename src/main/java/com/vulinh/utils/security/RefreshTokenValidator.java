package com.vulinh.utils.security;

import com.vulinh.data.dto.carrier.DecodedJwtPayloadCarrier;
import org.springframework.lang.NonNull;

public interface RefreshTokenValidator {

  @NonNull
  DecodedJwtPayloadCarrier validateRefreshToken(String refreshToken);
}
