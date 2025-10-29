package com.vulinh.service.token;

import com.vulinh.data.constant.TokenType;
import com.vulinh.data.dto.carrier.DecodedJwtPayloadCarrier;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenValidator {

  final TokenValidator tokenValidator;

  @NonNull
  public DecodedJwtPayloadCarrier validateRefreshToken(String refreshToken) {
    return tokenValidator.validateToken(refreshToken, TokenType.REFRESH_TOKEN);
  }
}
