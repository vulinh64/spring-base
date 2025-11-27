package com.vulinh.utils;

import module java.base;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtUtils {

  static final String AUTHORIZED_PARTY_CLAIM = "azp";
  static final String RESOURCE_ACCESS_CLAIM = "resource_access";

  @SuppressWarnings("unchecked")
  public static AbstractAuthenticationToken parseAuthoritiesByCustomClaims(
      Jwt jwt, @NonNull String clientName) {
    if (!clientName.equals(jwt.getClaim(AUTHORIZED_PARTY_CLAIM))) {
      throw new BadJwtException("Invalid authorized party");
    }

    if (!jwt.hasClaim(RESOURCE_ACCESS_CLAIM)) {
      throw new BadJwtException("Missing resource access claim");
    }

    var resourceAccess = jwt.getClaimAsMap(RESOURCE_ACCESS_CLAIM);

    if (!resourceAccess.containsKey(clientName)) {
      throw new BadJwtException("Missing client name claim");
    }

    var clientRoleContainer = (Map<String, Object>) resourceAccess.get(clientName);

    var roles = (List<String>) clientRoleContainer.getOrDefault("roles", Collections.emptyList());

    return new JwtAuthenticationToken(
        jwt, roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
  }
}
