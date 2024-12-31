package com.vulinh.service.auth0;

import com.vulinh.configuration.SecurityConfigProperties;
import com.vulinh.data.dto.security.AccessTokenContainer;
import com.vulinh.data.dto.security.AccessTokenType;
import com.vulinh.data.dto.security.TokenResponse;
import com.vulinh.data.entity.Users;
import com.vulinh.data.entity.ids.UserSessionId;
import com.vulinh.utils.security.AccessTokenGenerator;
import com.vulinh.utils.security.Auth0Utils;
import com.vulinh.utils.security.RefreshTokenGenerator;
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

  private final RefreshTokenGenerator refreshTokenGenerator;

  @Override
  public AccessTokenContainer generateAccessToken(Users users, UUID sessionId) {
    var issuedAt = Instant.now();
    var userId = users.getId();
    var userSessionId = UserSessionId.of(userId, sessionId);
    var refreshTokenContainer = refreshTokenGenerator.generateRefreshToken(userSessionId, issuedAt);

    return AccessTokenContainer.builder()
        .tokenResponse(
            TokenResponse.builder()
                .accessToken(
                    Auth0Utils.buildTokenCommonParts(
                            userSessionId,
                            issuedAt,
                            securityConfigProperties.issuer(),
                            securityConfigProperties.jwtDuration(),
                            AccessTokenType.ACCESS_TOKEN)
                        .withIssuedAt(issuedAt)
                        .sign(Auth0Utils.getAlgorithm(securityConfigProperties)))
                .refreshToken(refreshTokenContainer.refreshToken())
                .build())
        .userId(userId)
        .sessionId(sessionId)
        .refreshTokenExpirationDate(refreshTokenContainer.expirationDate())
        .build();
  }
}
