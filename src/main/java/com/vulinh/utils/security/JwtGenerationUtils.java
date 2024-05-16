package com.vulinh.utils.security;

import com.vulinh.configuration.SecurityConfigProperties;
import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.security.AccessToken;
import com.vulinh.data.entity.Users;
import com.vulinh.data.entity.Users_;
import com.vulinh.exception.CustomSecurityException;
import com.vulinh.utils.JsonUtils;
import com.vulinh.utils.SecurityUtils;
import io.jsonwebtoken.Jwts;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtGenerationUtils {

  private final SecurityConfigProperties securityConfigProperties;

  @SneakyThrows
  public AccessToken generateAccessToken(Users matchedUser) {
    try {
      var issuedAt = OffsetDateTime.now();

      var expiration = issuedAt.plus(securityConfigProperties.jwtDuration());

      var accessToken =
          Jwts.builder()
              .setSubject(matchedUser.getId())
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
    } catch (Exception exception) {
      throw new CustomSecurityException(
          "Invalid credentials issuer",
          CommonMessage.MESSAGE_INVALID_CREDENTIALS_ISSUER,
          exception);
    }
  }

  private Map<String, Object> toClaims(Users user) {
    return Map.ofEntries(Map.entry(Users_.USERNAME, user.getUsername()));
  }

  private static Date toDate(OffsetDateTime localDateTime) {
    return Date.from(localDateTime.toInstant());
  }

  @SneakyThrows
  private static byte[] serialize(Map<String, ?> payload) {
    return JsonUtils.delegate().writeValueAsBytes(payload);
  }
}
