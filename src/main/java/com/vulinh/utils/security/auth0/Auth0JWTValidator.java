package com.vulinh.utils.security.auth0;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.vulinh.configuration.SecurityConfigProperties;
import com.vulinh.data.dto.security.JwtPayload;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.utils.security.AccessTokenValidator;
import java.util.UUID;
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
  public JwtPayload validateAccessToken(String accessToken) {
    try {
      var decodedJWT = getJwtVerifier(securityConfigProperties).verify(accessToken);

      return JwtPayload.builder()
          .issuer(decodedJWT.getIssuer())
          .userId(UUID.fromString(decodedJWT.getClaim(Utils.USER_ID_CLAIM).asString()))
          .sessionId(UUID.fromString(decodedJWT.getClaim(Utils.SESSION_ID_CLAIM).asString()))
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
          JWT.require(Utils.getAlgorithm(securityConfigProperties))
              .withIssuer(securityConfigProperties.issuer())
              .build();
    }

    return jwtVerifier;
  }
}
