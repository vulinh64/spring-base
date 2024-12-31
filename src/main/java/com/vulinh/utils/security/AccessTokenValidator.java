package com.vulinh.utils.security;

import com.vulinh.data.dto.security.JwtPayload;
import org.springframework.lang.NonNull;

public interface AccessTokenValidator {

  @NonNull
  JwtPayload validateAccessToken(String accessToken);
}
