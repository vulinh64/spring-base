package com.vulinh.utils.security;

import com.vulinh.data.dto.carrier.DecodedJwtPayloadCarrier;
import org.springframework.lang.NonNull;

public interface AccessTokenValidator {

  @NonNull
  DecodedJwtPayloadCarrier validateAccessToken(String accessToken);
}
