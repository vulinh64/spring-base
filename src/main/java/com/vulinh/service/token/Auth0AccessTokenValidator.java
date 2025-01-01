package com.vulinh.service.token;

import com.vulinh.data.dto.security.DecodedJwtPayload;
import com.vulinh.data.dto.security.TokenType;
import com.vulinh.utils.security.AccessTokenValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Auth0AccessTokenValidator implements AccessTokenValidator {

  private final Auth0TokenValidator auth0TokenValidator;

  @Override
  @NonNull
  public DecodedJwtPayload validateAccessToken(String accessToken) {
    return auth0TokenValidator.validateToken(accessToken, TokenType.ACCESS_TOKEN);
  }
}