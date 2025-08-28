package com.vulinh.utils.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.vulinh.configuration.ApplicationProperties.SecurityProperties;
import com.vulinh.data.constant.TokenType;
import com.vulinh.data.entity.ids.QUserSessionId;
import com.vulinh.data.entity.ids.UserSessionId;
import com.vulinh.exception.AuthorizationException;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.utils.PredicateBuilder;
import com.vulinh.utils.RSAUtils;
import java.time.Duration;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Auth0Utils {

  public static final String USER_ID_CLAIM =
      PredicateBuilder.getFieldName(QUserSessionId.userSessionId.userId);

  public static final String SESSION_ID_CLAIM =
      PredicateBuilder.getFieldName(QUserSessionId.userSessionId.sessionId);

  public static final String TOKEN_TYPE = "tokenType";

  private static Algorithm rsaAlgorithm;
  private static JWTVerifier jwtVerifier;

  public static Algorithm getAlgorithm(SecurityProperties securityProperties) {
    if (rsaAlgorithm == null) {
      rsaAlgorithm =
          Algorithm.RSA512(
              RSAUtils.generatePublicKey(securityProperties.publicKey()),
              RSAUtils.generatePrivateKey(securityProperties.privateKey()));
    }

    return rsaAlgorithm;
  }

  public static JWTCreator.Builder buildTokenCommonParts(
      UserSessionId userSessionId,
      Instant issuedAt,
      String issuer,
      Duration ttl,
      TokenType tokenType) {
    return JWT.create()
        .withIssuer(issuer)
        .withExpiresAt(issuedAt.plus(ttl))
        .withClaim(USER_ID_CLAIM, String.valueOf(userSessionId.userId()))
        .withClaim(SESSION_ID_CLAIM, String.valueOf(userSessionId.sessionId()))
        .withClaim(TOKEN_TYPE, tokenType.name());
  }

  public static JWTVerifier getJwtVerifier(SecurityProperties securityProperties) {
    if (jwtVerifier == null) {
      jwtVerifier =
          JWT.require(getAlgorithm(securityProperties))
              .withIssuer(securityProperties.issuer())
              .build();
    }

    return jwtVerifier;
  }

  public static String claimAsString(DecodedJWT decodedJWT, String claimName) {
    var claimNode = decodedJWT.getClaim(claimName);

    if (claimNode.isMissing() || claimNode.isNull()) {
      throw AuthorizationException.invalidAuthorization(
          "Claim %s is missing".formatted(claimName),
          ServiceErrorCode.MESSAGE_INVALID_AUTHORIZATION);
    }

    return claimNode.asString();
  }

  public static String parseBearerToken(String token) {
    if (StringUtils.isBlank(token)) {
      throw AuthorizationException.invalidAuthorization(
          "Session ID %s for user ID %s did not exist or has been invalidated",
          ServiceErrorCode.MESSAGE_INVALID_SESSION);
    }

    return token.startsWith("Bearer") ? token.substring(7) : token;
  }
}
