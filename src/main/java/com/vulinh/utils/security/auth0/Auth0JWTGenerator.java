package com.vulinh.utils.security.auth0;

import com.auth0.jwt.JWT;
import com.vulinh.configuration.SecurityConfigProperties;
import com.vulinh.data.dto.security.AccessTokenContainer;
import com.vulinh.data.dto.security.TokenResponse;
import com.vulinh.data.entity.Users;
import com.vulinh.utils.security.AccessTokenGenerator;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
@RequiredArgsConstructor
public class Auth0JWTGenerator implements AccessTokenGenerator {

  private final SecurityConfigProperties securityConfigProperties;

  @Override
  public AccessTokenContainer generateAccessToken(Users users, UUID sessionId) {
    var issuedAt = Instant.now();
    var userId = users.getId();

    return AccessTokenContainer.builder()
        .tokenResponse(
            TokenResponse.builder()
                .accessToken(
                    JWT.create()
                        .withIssuer(securityConfigProperties.issuer())
                        .withExpiresAt(issuedAt.plus(securityConfigProperties.jwtDuration()))
                        .withClaim(Utils.USER_ID_CLAIM, String.valueOf(userId))
                        .withClaim(Utils.SESSION_ID_CLAIM, String.valueOf(sessionId))
                        .withIssuedAt(issuedAt)
                        .sign(Utils.getAlgorithm(securityConfigProperties)))
                .build())
        .userId(userId)
        .sessionId(sessionId)
        .build();
  }
}
