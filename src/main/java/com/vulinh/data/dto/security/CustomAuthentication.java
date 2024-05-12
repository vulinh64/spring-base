package com.vulinh.data.dto.security;

import com.vulinh.data.dto.user.UserBasicDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.io.Serial;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;

@Getter
@EqualsAndHashCode(callSuper = true)
public class CustomAuthentication extends AbstractAuthenticationToken {

  @Serial private static final long serialVersionUID = 8271426933575028085L;

  private final JwtPayload principal;
  private final UserBasicDTO userDTO;

  public static CustomAuthentication of(
      JwtPayload principal, UserBasicDTO userBasicDTO, HttpServletRequest httpServletRequest) {
    return new CustomAuthentication(principal, userBasicDTO, httpServletRequest);
  }

  private CustomAuthentication(
      JwtPayload principal, UserBasicDTO userBasicDTO, HttpServletRequest httpServletRequest) {
    super(principal.userRoles().stream().map(SimpleGrantedAuthority::new).toList());
    this.principal = principal;
    userDTO = userBasicDTO;
    setDetails(
        new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(
            httpServletRequest, getAuthorities()));
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return null;
  }
}
