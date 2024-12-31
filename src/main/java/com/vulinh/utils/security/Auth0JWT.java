package com.vulinh.utils.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.vulinh.configuration.SecurityConfigProperties;
import com.vulinh.data.dto.security.AccessTokenContainer;
import com.vulinh.data.dto.security.JwtPayload;
import com.vulinh.data.dto.security.TokenResponse;
import com.vulinh.data.entity.Users;
import com.vulinh.data.entity.ids.UserSessionId;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.utils.SecurityUtils;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
@RequiredArgsConstructor
public class Auth0JWT implements AccessTokenGenerator, AccessTokenValidator {

  private static final ExceptionFactory EXCEPTION_FACTORY = ExceptionFactory.INSTANCE;

  private static Algorithm rsaAlgorithm;
  private static JWTVerifier jwtVerifier;

  private final SecurityConfigProperties securityConfigProperties;

  @Override
  public AccessTokenContainer generateAccessToken(Users users, UUID sessionId) {
    var issuedAt = Instant.now();
    var expiration = issuedAt.plus(securityConfigProperties.jwtDuration());
    var userId = users.getId();
    var userSessionId = UserSessionId.of(userId, sessionId);

    return AccessTokenContainer.builder()
        .tokenResponse(
            TokenResponse.builder()
                .accessToken(
                    JWT.create()
                        .withIssuer(securityConfigProperties.issuer())
                        .withExpiresAt(expiration)
                        .withClaim("userId", String.valueOf(userSessionId.userId()))
                        .withClaim("sessionId", String.valueOf(userSessionId.sessionId()))
                        .withIssuedAt(issuedAt)
                        .sign(getAlgorithm(securityConfigProperties)))
                .build())
        .userId(userId)
        .sessionId(sessionId)
        .build();
  }

  @Override
  @SneakyThrows
  public JwtPayload validateAccessToken(String accessToken) {
    try {
      var decodedJWT = getJwtVerifier(securityConfigProperties).verify(accessToken);

      return JwtPayload.builder()
          .issuer(decodedJWT.getIssuer())
          .userId(UUID.fromString(decodedJWT.getClaim("userId").asString()))
          .sessionId(UUID.fromString(decodedJWT.getClaim("sessionId").asString()))
          .build();
    } catch (TokenExpiredException tokenExpiredException) {
      throw EXCEPTION_FACTORY.expiredAccessToken(tokenExpiredException);
    } catch (Exception exception) {
      throw EXCEPTION_FACTORY.parsingPublicKeyError(exception);
    }
  }

  private static JWTVerifier getJwtVerifier(SecurityConfigProperties securityConfigProperties) {
    if (jwtVerifier == null) {
      jwtVerifier =
          JWT.require(getAlgorithm(securityConfigProperties))
              .withIssuer(securityConfigProperties.issuer())
              .build();
    }

    return jwtVerifier;
  }

  private static Algorithm getAlgorithm(SecurityConfigProperties securityConfigProperties) {
    if (rsaAlgorithm == null) {
      rsaAlgorithm =
          Algorithm.RSA512(
              SecurityUtils.generatePublicKey(securityConfigProperties.publicKey()),
              SecurityUtils.generatePrivateKey(securityConfigProperties.privateKey()));
    }

    return rsaAlgorithm;
  }
}
