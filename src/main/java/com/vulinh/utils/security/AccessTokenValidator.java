package com.vulinh.utils.security;

import com.vulinh.data.dto.security.JwtPayload;

public interface AccessTokenValidator {

  JwtPayload validateAccessToken(String accessToken);
}
