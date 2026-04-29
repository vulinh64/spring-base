package com.vulinh.utils;

import module java.base;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtUtils {

  static final String ROLES_CLAIM = "roles";

  public static AbstractAuthenticationToken parseAuthoritiesByCustomClaims(Jwt jwt) {
    var roles = jwt.getClaimAsStringList(ROLES_CLAIM);

    if (roles == null) {
      roles = Collections.emptyList();
    }

    return new JwtAuthenticationToken(
        jwt, roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
  }
}
