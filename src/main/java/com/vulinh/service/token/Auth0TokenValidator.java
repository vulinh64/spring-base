package com.vulinh.service.token;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.vulinh.configuration.SecurityConfigProperties;
import com.vulinh.data.dto.security.DecodedJwtPayload;
import com.vulinh.data.dto.security.TokenType;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.utils.security.Auth0Utils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class Auth0TokenValidator {

  private static final ExceptionFactory EXCEPTION_FACTORY = ExceptionFactory.INSTANCE;

  private final SecurityConfigProperties securityConfigProperties;

  DecodedJwtPayload validateToken(String refreshToken, TokenType expectedTokenType) {
    try {
      var decodedJWT = Auth0Utils.getJwtVerifier(securityConfigProperties).verify(refreshToken);

      var actualTokenType =
          TokenType.valueOf(Auth0Utils.claimAsString(decodedJWT, Auth0Utils.TOKEN_TYPE));

      if (actualTokenType != expectedTokenType) {
        throw EXCEPTION_FACTORY.invalidTokenType(actualTokenType, TokenType.REFRESH_TOKEN);
      }

      return DecodedJwtPayload.builder()
          .userId(UUID.fromString(Auth0Utils.claimAsString(decodedJWT, Auth0Utils.USER_ID_CLAIM)))
          .sessionId(
              UUID.fromString(Auth0Utils.claimAsString(decodedJWT, Auth0Utils.SESSION_ID_CLAIM)))
          .build();
    } catch (TokenExpiredException tokenExpiredException) {
      throw EXCEPTION_FACTORY.expiredAccessToken(tokenExpiredException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw EXCEPTION_FACTORY.parsingPublicKeyError(illegalArgumentException);
    }
  }
}
