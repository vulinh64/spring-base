package com.vulinh.exception;

import com.vulinh.data.dto.bundle.CommonMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionBuilder {

  public static CommonException invalidPassword() {
    return new CommonException("Invalid password", CommonMessage.MESSAGE_INVALID_PASSWORD);
  }

  public static CustomSecurityException invalidCredentials() {
    return new CustomSecurityException(
        "Invalid user credentials", CommonMessage.MESSAGE_INVALID_CREDENTIALS);
  }

  public static CustomSecurityException invalidAuthorization() {
    return invalidAuthorization(null);
  }

  public static CustomSecurityException invalidAuthorization(Throwable exception) {
    return new CustomSecurityException(
        "Invalid authorization", CommonMessage.MESSAGE_INVALID_AUTHORIZATION, exception);
  }
}
