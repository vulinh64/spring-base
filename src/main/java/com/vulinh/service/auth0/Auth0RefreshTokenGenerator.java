package com.vulinh.service.auth0;

import com.vulinh.configuration.SecurityConfigProperties;
import com.vulinh.data.dto.security.AccessTokenType;
import com.vulinh.data.dto.security.RefreshTokenContainer;
import com.vulinh.data.entity.ids.UserSessionId;
import com.vulinh.utils.security.Auth0Utils;
import com.vulinh.utils.security.RefreshTokenGenerator;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Primary
@Component
@RequiredArgsConstructor
public class Auth0RefreshTokenGenerator implements RefreshTokenGenerator {

  private final SecurityConfigProperties securityConfigProperties;

  @Override
  @NonNull
  public RefreshTokenContainer generateRefreshToken(UserSessionId userSessionId, Instant issuedAt) {
    var ttl = securityConfigProperties.refreshJwtDuration();

    return RefreshTokenContainer.builder()
        .expirationDate(issuedAt.plus(ttl))
        .refreshToken(
            Auth0Utils.buildTokenCommonParts(
                    userSessionId,
                    issuedAt,
                    securityConfigProperties.issuer(),
                    ttl,
                    AccessTokenType.REFRESH_TOKEN)
                .sign(Auth0Utils.getAlgorithm(securityConfigProperties)))
        .build();
  }
}
