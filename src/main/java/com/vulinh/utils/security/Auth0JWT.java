package com.vulinh.utils.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.vulinh.configuration.SecurityConfigProperties;
import com.vulinh.data.dto.security.AccessToken;
import com.vulinh.data.dto.security.JwtPayload;
import com.vulinh.data.entity.Users;
import com.vulinh.data.entity.Users_;
import com.vulinh.exception.ExceptionBuilder;
import com.vulinh.utils.SecurityUtils;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
@RequiredArgsConstructor
public class Auth0JWT implements AccessTokenGenerator, AccessTokenValidator {

  private static Algorithm rsaAlgorithm;
  private static JWTVerifier jwtVerifier;

  private final SecurityConfigProperties securityConfigProperties;

  @Override
  public AccessToken generateAccessToken(Users users) {
    var issuedAt = Instant.now();
    var expiration = issuedAt.plus(securityConfigProperties.jwtDuration());

    return AccessToken.builder()
        .accessToken(
            JWT.create()
                .withIssuer(securityConfigProperties.issuer())
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiration)
                .withSubject(users.getId())
                .withClaim(Users_.USERNAME, users.getUsername())
                .sign(getAlgorithm(securityConfigProperties)))
        .issuedAt(issuedAt)
        .expiration(expiration)
        .build();
  }

  @Override
  @SneakyThrows
  public JwtPayload validateAccessToken(String accessToken) {
    try {
      var decodedJWT = getJwtVerifier(securityConfigProperties).verify(accessToken);

      return JwtPayload.builder()
          .issuer(decodedJWT.getIssuer())
          .subject(decodedJWT.getSubject())
          .username(decodedJWT.getClaim(Users_.USERNAME).asString())
          .build();
    } catch (TokenExpiredException tokenExpiredException) {
      throw ExceptionBuilder.expiredAccessToken(tokenExpiredException);
    } catch (Exception exception) {
      throw ExceptionBuilder.parsingPublicKeyError(exception);
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
