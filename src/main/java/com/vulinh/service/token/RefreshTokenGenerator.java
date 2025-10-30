package com.vulinh.service.token;

import module java.base;

import com.vulinh.configuration.ApplicationProperties;
import com.vulinh.data.constant.TokenType;
import com.vulinh.data.dto.carrier.RefreshTokenCarrier;
import com.vulinh.data.entity.ids.UserSessionId;
import com.vulinh.utils.security.Auth0Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenGenerator {

  final ApplicationProperties applicationProperties;

  @NonNull
  public RefreshTokenCarrier generateRefreshToken(UserSessionId userSessionId, Instant issuedAt) {
    var securityProperties = applicationProperties.security();

    var ttl = securityProperties.refreshJwtDuration();

    return RefreshTokenCarrier.builder()
        .expirationDate(issuedAt.plus(ttl))
        .refreshToken(
            Auth0Utils.buildTokenCommonParts(
                    securityProperties, userSessionId, issuedAt, TokenType.REFRESH_TOKEN)
                .sign(Auth0Utils.getAlgorithm(securityProperties)))
        .build();
  }
}
