package com.vulinh.utils.security;

import com.vulinh.data.dto.security.DecodedJwtPayload;
import org.springframework.lang.NonNull;

public interface AccessTokenValidator {

  @NonNull
  DecodedJwtPayload validateAccessToken(String accessToken);
}
