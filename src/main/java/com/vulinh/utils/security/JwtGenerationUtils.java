package com.vulinh.utils.security;

import com.vulinh.configuration.SecurityConfigProperties;
import com.vulinh.data.dto.security.AccessToken;
import com.vulinh.data.entity.QUsers;
import com.vulinh.data.entity.Users;
import com.vulinh.utils.JsonUtils;
import com.vulinh.utils.QueryDSLPredicateBuilder;
import com.vulinh.utils.SecurityUtils;
import io.jsonwebtoken.Jwts;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

/**
 * @deprecated In favor of auth0's {@link Auth0JWT}
 */
@Component
@RequiredArgsConstructor
@Deprecated(forRemoval = true)
public class JwtGenerationUtils implements AccessTokenGenerator {

  private final SecurityConfigProperties securityConfigProperties;

  @Override
  @SneakyThrows
  public AccessToken generateAccessToken(Users matchedUser) {
    var issuedAt = OffsetDateTime.now();

    var expiration = issuedAt.plus(securityConfigProperties.jwtDuration());

    var accessToken =
        Jwts.builder()
            .setSubject(matchedUser.getId().toString())
            .setIssuer("spring-base-service")
            .setIssuedAt(toDate(issuedAt))
            .setExpiration(toDate(expiration))
            .addClaims(toClaims(matchedUser))
            .signWith(SecurityUtils.generatePrivateKey(securityConfigProperties.privateKey()))
            .serializeToJsonWith(JwtGenerationUtils::serialize)
            .compact();

    return AccessToken.builder()
        .issuedAt(issuedAt)
        .expiration(expiration)
        .accessToken(accessToken)
        .build();
  }

  private Map<String, Object> toClaims(Users user) {
    return Map.ofEntries(
        Map.entry(
            QueryDSLPredicateBuilder.getFieldName(QUsers.users.username), user.getUsername()));
  }

  private static Date toDate(OffsetDateTime localDateTime) {
    return Date.from(localDateTime.toInstant());
  }

  @SneakyThrows
  private static byte[] serialize(Map<String, ?> payload) {
    return JsonUtils.delegate().writeValueAsBytes(payload);
  }
}
