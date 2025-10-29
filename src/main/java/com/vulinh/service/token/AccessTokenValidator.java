package com.vulinh.service.token;

import com.vulinh.data.constant.TokenType;
import com.vulinh.data.dto.carrier.DecodedJwtPayloadCarrier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessTokenValidator {

  final TokenValidator tokenValidator;

  @NonNull
  public DecodedJwtPayloadCarrier validateAccessToken(String accessToken) {
    return tokenValidator.validateToken(accessToken, TokenType.ACCESS_TOKEN);
  }
}
