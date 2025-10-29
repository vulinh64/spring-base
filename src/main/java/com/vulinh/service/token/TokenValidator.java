package com.vulinh.service.token;

import module java.base;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.vulinh.configuration.ApplicationProperties;
import com.vulinh.data.constant.TokenType;
import com.vulinh.data.dto.carrier.DecodedJwtPayloadCarrier;
import com.vulinh.exception.AuthorizationException;
import com.vulinh.exception.SecurityConfigurationException;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.utils.security.Auth0Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class TokenValidator {

  final ApplicationProperties securityConfigProperties;

  public DecodedJwtPayloadCarrier validateToken(String refreshToken, TokenType expectedTokenType) {
    try {
      var decodedJWT =
          Auth0Utils.getJwtVerifier(securityConfigProperties.security()).verify(refreshToken);

      var actualTokenType =
          TokenType.valueOf(Auth0Utils.claimAsString(decodedJWT, Auth0Utils.TOKEN_TYPE));

      if (actualTokenType != expectedTokenType) {
        throw AuthorizationException.invalidAuthorization(
            "Expected token type %s, but actual token was %s"
                .formatted(TokenType.REFRESH_TOKEN, actualTokenType),
            ServiceErrorCode.MESSAGE_INVALID_TOKEN_TYPE);
      }

      return DecodedJwtPayloadCarrier.builder()
          .userId(UUID.fromString(Auth0Utils.claimAsString(decodedJWT, Auth0Utils.USER_ID_CLAIM)))
          .sessionId(
              UUID.fromString(Auth0Utils.claimAsString(decodedJWT, Auth0Utils.SESSION_ID_CLAIM)))
          .build();
    } catch (JWTDecodeException jwtDecodeException) {
      throw AuthorizationException.invalidAuthorization(
          "Invalid token format", ServiceErrorCode.MESSAGE_INVALID_TOKEN, jwtDecodeException);
    } catch (TokenExpiredException tokenExpiredException) {
      throw AuthorizationException.invalidAuthorization(
          "Access token expired",
          ServiceErrorCode.MESSAGE_CREDENTIALS_EXPIRED,
          tokenExpiredException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw SecurityConfigurationException.configurationException(
          "Invalid public key",
          ServiceErrorCode.MESSAGE_INVALID_PUBLIC_KEY_CONFIG,
          illegalArgumentException);
    }
  }
}
