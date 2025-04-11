package com.vulinh.service.token;

import com.vulinh.configuration.data.SecurityConfigProperties;
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

  private final SecurityConfigProperties securityConfigProperties;

  @Override
  @NonNull
  public RefreshTokenCarrier generateRefreshToken(UserSessionId userSessionId, Instant issuedAt) {
    var ttl = securityConfigProperties.refreshJwtDuration();

    return RefreshTokenCarrier.builder()
        .expirationDate(issuedAt.plus(ttl))
        .refreshToken(
            Auth0Utils.buildTokenCommonParts(
                    userSessionId,
                    issuedAt,
                    securityConfigProperties.issuer(),
                    ttl,
                    TokenType.REFRESH_TOKEN)
                .sign(Auth0Utils.getAlgorithm(securityConfigProperties)))
        .build();
  }
}
