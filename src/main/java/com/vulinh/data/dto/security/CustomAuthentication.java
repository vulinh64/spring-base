package com.vulinh.data.dto.security;

import com.vulinh.data.dto.user.RoleDTO;
import com.vulinh.data.dto.user.UserBasicDTO;
import java.io.Serial;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@EqualsAndHashCode(callSuper = true)
public class CustomAuthentication extends AbstractAuthenticationToken {

  @Serial private static final long serialVersionUID = 8271426933575028085L;

  private final UserBasicDTO principal;

  public CustomAuthentication(UserBasicDTO userBasicDTO) {
    super(
        userBasicDTO.userRoles().stream()
            .map(RoleDTO::id)
            .map(String::valueOf)
            .map(SimpleGrantedAuthority::new)
            .toList());

    principal = userBasicDTO;
    setAuthenticated(true);
  }

  public CustomAuthentication addDetails(Object detail) {
    setDetails(detail);

    return this;
  }

  @Override
  public Object getCredentials() {
    return null;
  }
}
