package com.vulinh.configuration;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public record JwtTypValidator(String expectedTyp) implements OAuth2TokenValidator<Jwt> {

  static final String TYP_CLAIM = "typ";

  @Override
  public OAuth2TokenValidatorResult validate(Jwt jwt) {
    if (expectedTyp.equals(jwt.getClaimAsString(TYP_CLAIM))) {
      return OAuth2TokenValidatorResult.success();
    }

    return OAuth2TokenValidatorResult.failure(
        new OAuth2Error(
            "invalid_token",
            "Required token type '%s' is missing".formatted(expectedTyp),
            null));
  }
}
