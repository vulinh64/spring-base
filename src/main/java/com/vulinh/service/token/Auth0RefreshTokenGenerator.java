package com.vulinh.service.token;

import com.vulinh.configuration.ApplicationProperties;
import com.vulinh.data.constant.TokenType;
import com.vulinh.data.dto.carrier.RefreshTokenCarrier;
import com.vulinh.data.entity.ids.UserSessionId;
import com.vulinh.utils.security.Auth0Utils;
import com.vulinh.utils.security.RefreshTokenGenerator;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Auth0RefreshTokenGenerator implements RefreshTokenGenerator {

  private final ApplicationProperties applicationProperties;

  @Override
  @NonNull
  public RefreshTokenCarrier generateRefreshToken(UserSessionId userSessionId, Instant issuedAt) {
    var securityProperties = applicationProperties.security();

    var ttl = securityProperties.refreshJwtDuration();

    return RefreshTokenCarrier.builder()
        .expirationDate(issuedAt.plus(ttl))
        .refreshToken(
            Auth0Utils.buildTokenCommonParts(
                    userSessionId,
                    issuedAt,
                    securityProperties.issuer(),
                    ttl,
                    TokenType.REFRESH_TOKEN)
                .sign(Auth0Utils.getAlgorithm(securityProperties)))
        .build();
  }
}
