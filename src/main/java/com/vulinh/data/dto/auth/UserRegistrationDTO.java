package com.vulinh.data.dto.auth;

import com.vulinh.constant.UserRole;
import java.util.Collection;
import java.util.Set;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record UserRegistrationDTO(
    String username, String password, String fullName, String email, Collection<String> userRoles) {

  public UserRegistrationDTO(
      String username,
      String password,
      String fullName,
      String email,
      Collection<String> userRoles) {
    this.username = username;
    this.password = password;
    this.fullName = fullName;
    this.email = email;
    this.userRoles = userRoles == null ? Set.of(UserRole.USER.name()) : userRoles;
  }
}
