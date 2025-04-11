package com.vulinh.data.dto.request;

import com.vulinh.data.constant.UserRole;
import java.util.Collection;
import java.util.Set;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record UserRegistrationRequest(
    String username, String password, String fullName, String email, Collection<String> userRoles) {

  public UserRegistrationRequest {
    userRoles = userRoles == null ? Set.of(UserRole.USER.name()) : userRoles;
  }
}
