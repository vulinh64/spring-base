package com.vulinh.data.dto.request;

import module java.base;

import com.vulinh.data.constant.UserRole;
import com.vulinh.utils.TextSanitizer;
import lombok.Builder;
import lombok.With;
import org.apache.commons.lang3.StringUtils;

@With
@Builder
public record UserRegistrationRequest(
    String username, String password, String fullName, String email, Collection<String> userRoles) {

  public UserRegistrationRequest {
    userRoles = userRoles == null ? Set.of(UserRole.USER.name()) : userRoles;
    fullName = TextSanitizer.sanitize(StringUtils.normalizeSpace(fullName));
  }
}
