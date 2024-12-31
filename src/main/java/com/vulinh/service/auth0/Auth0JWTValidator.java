package com.vulinh.service.auth0;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.vulinh.configuration.SecurityConfigProperties;
import com.vulinh.data.dto.security.AccessTokenType;
import com.vulinh.data.dto.security.JwtPayload;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.utils.security.AccessTokenValidator;
import java.util.UUID;

import com.vulinh.utils.security.Auth0Utils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
@RequiredArgsConstructor
public class Auth0JWTValidator implements AccessTokenValidator {

  private static final ExceptionFactory EXCEPTION_FACTORY = ExceptionFactory.INSTANCE;

  private static JWTVerifier jwtVerifier;

  private final SecurityConfigProperties securityConfigProperties;

  @Override
  @SneakyThrows
  @NonNull
  public JwtPayload validateAccessToken(String accessToken) {
    try {
      var decodedJWT = getJwtVerifier(securityConfigProperties).verify(accessToken);

      var accessTokenType = AccessTokenType.valueOf(claimAsString(decodedJWT, Auth0Utils.TOKEN_TYPE));

      if (accessTokenType != AccessTokenType.ACCESS_TOKEN) {
        throw EXCEPTION_FACTORY.invalidTokenType(accessTokenType, AccessTokenType.ACCESS_TOKEN);
      }

      return JwtPayload.builder()
          .issuer(decodedJWT.getIssuer())
          .userId(UUID.fromString(claimAsString(decodedJWT, Auth0Utils.USER_ID_CLAIM)))
          .sessionId(UUID.fromString(claimAsString(decodedJWT, Auth0Utils.SESSION_ID_CLAIM)))
          .tokenType(accessTokenType)
          .build();
    } catch (TokenExpiredException tokenExpiredException) {
      throw EXCEPTION_FACTORY.expiredAccessToken(tokenExpiredException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw EXCEPTION_FACTORY.parsingPublicKeyError(illegalArgumentException);
    }
  }

  private static String claimAsString(DecodedJWT decodedJWT, String claimName) {
    var claimNode = decodedJWT.getClaim(claimName);

    if (claimNode.isMissing() || claimNode.isNull()) {
      throw EXCEPTION_FACTORY.missingClaim(claimName);
    }

    return claimNode.asString();
  }

  private static JWTVerifier getJwtVerifier(SecurityConfigProperties securityConfigProperties) {
    if (jwtVerifier == null) {
      jwtVerifier =
          JWT.require(Auth0Utils.getAlgorithm(securityConfigProperties))
              .withIssuer(securityConfigProperties.issuer())
              .build();
    }

    return jwtVerifier;
  }
}
