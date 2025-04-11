package com.vulinh.service.token;

import com.vulinh.data.constant.TokenType;
import com.vulinh.data.dto.carrier.DecodedJwtPayloadCarrier;
import com.vulinh.utils.security.RefreshTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Auth0RefreshTokenValidator implements RefreshTokenValidator {

  private final Auth0TokenValidator auth0TokenValidator;

  @Override
  @NonNull
  public DecodedJwtPayloadCarrier validateRefreshToken(String refreshToken) {
    return auth0TokenValidator.validateToken(refreshToken, TokenType.REFRESH_TOKEN);
  }
}
