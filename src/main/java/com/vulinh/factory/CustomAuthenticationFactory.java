package com.vulinh.factory;

import com.vulinh.data.dto.security.CustomAuthentication;
import com.vulinh.data.dto.user.UserBasicDTO;

@SuppressWarnings("java:S6548")
public enum CustomAuthenticationFactory {
  INSTANCE;

  public CustomAuthentication fromUserBasicDTO(UserBasicDTO userDTOAsPrincipal) {
    return new CustomAuthentication(userDTOAsPrincipal);
  }
}
