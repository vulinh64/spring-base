package com.vulinh.configuration.data;

import module java.base;

import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.data.dto.response.data.RoleData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@EqualsAndHashCode(callSuper = true)
public class CustomAuthentication extends AbstractAuthenticationToken {

  @Serial private static final long serialVersionUID = 8271426933575028085L;

  final UserBasicResponse principal;

  public CustomAuthentication(UserBasicResponse userBasicResponse) {
    super(
        userBasicResponse.userRoles().stream()
            .map(RoleData::id)
            .map(String::valueOf)
            .map(SimpleGrantedAuthority::new)
            .toList());

    principal = userBasicResponse;
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
