package com.vulinh.service.token;

import com.vulinh.configuration.data.SecurityConfigProperties;
import com.vulinh.data.constant.TokenType;
import com.vulinh.data.dto.carrier.AccessTokenCarrier;
import com.vulinh.data.dto.carrier.TokenResponse;
import com.vulinh.data.entity.ids.UserSessionId;
import com.vulinh.utils.security.AccessTokenGenerator;
import com.vulinh.utils.security.Auth0Utils;
import com.vulinh.utils.security.RefreshTokenGenerator;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Auth0AccessTokenGenerator implements AccessTokenGenerator {

  private final SecurityConfigProperties securityConfigProperties;

  private final RefreshTokenGenerator refreshTokenGenerator;

  @Override
  @NonNull
  public AccessTokenCarrier generateAccessToken(UUID userId, UUID sessionId, Instant issuedAt) {
    var userSessionId = UserSessionId.of(userId, sessionId);
    var refreshTokenContainer = refreshTokenGenerator.generateRefreshToken(userSessionId, issuedAt);

    return AccessTokenCarrier.builder()
        .tokenResponse(
            TokenResponse.builder()
                .accessToken(
                    Auth0Utils.buildTokenCommonParts(
                            userSessionId,
                            issuedAt,
                            securityConfigProperties.issuer(),
                            securityConfigProperties.jwtDuration(),
                            TokenType.ACCESS_TOKEN)
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
