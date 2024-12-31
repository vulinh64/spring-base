package com.vulinh.utils.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.vulinh.configuration.SecurityConfigProperties;
import com.vulinh.data.dto.security.AccessTokenType;
import com.vulinh.data.entity.ids.QUserSessionId;
import com.vulinh.data.entity.ids.UserSessionId;
import com.vulinh.utils.PredicateBuilder;
import com.vulinh.utils.SecurityUtils;
import java.time.Duration;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Auth0Utils {

  public static final String USER_ID_CLAIM =
      PredicateBuilder.getFieldName(QUserSessionId.userSessionId.userId);

  public static final String SESSION_ID_CLAIM =
      PredicateBuilder.getFieldName(QUserSessionId.userSessionId.sessionId);

  public static final String TOKEN_TYPE = "tokenType";

  private static Algorithm rsaAlgorithm;

  public static Algorithm getAlgorithm(SecurityConfigProperties securityConfigProperties) {
    if (rsaAlgorithm == null) {
      rsaAlgorithm =
          Algorithm.RSA512(
              SecurityUtils.generatePublicKey(securityConfigProperties.publicKey()),
              SecurityUtils.generatePrivateKey(securityConfigProperties.privateKey()));
    }

    return rsaAlgorithm;
  }

  public static JWTCreator.Builder buildTokenCommonParts(
      UserSessionId userSessionId,
      Instant issuedAt,
      String issuer,
      Duration ttl,
      AccessTokenType accessTokenType) {
    return JWT.create()
        .withIssuer(issuer)
        .withExpiresAt(issuedAt.plus(ttl))
        .withClaim(USER_ID_CLAIM, String.valueOf(userSessionId.userId()))
        .withClaim(SESSION_ID_CLAIM, String.valueOf(userSessionId.sessionId()))
        .withClaim(TOKEN_TYPE, accessTokenType.name());
  }
}
