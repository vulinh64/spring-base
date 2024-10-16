package com.vulinh.exception;

import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.message.WithHttpStatusCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionBuilder {

  public static CommonException entityNotFound(String message, String entityName) {
    return new CommonException(message, CommonMessage.MESSAGE_INVALID_ENTITY_ID, null, entityName);
  }

  public static CommonException invalidAuthorization() {
    return invalidAuthorization(null);
  }

  public static CommonException invalidAuthorization(Throwable exception) {
    return buildCommonException(
        "Invalid authorization", CommonMessage.MESSAGE_INVALID_AUTHORIZATION, exception);
  }

  public static CommonException parsingPublicKeyError(Throwable throwable) {
    return buildCommonException(
        "Parsing public key error", CommonMessage.MESSAGE_INVALID_PUBLIC_KEY_CONFIG, throwable);
  }

  public static CommonException expiredAccessToken(Throwable tokenExpiredException) {
    return buildCommonException(
        "Access token expired", CommonMessage.MESSAGE_CREDENTIALS_EXPIRED, tokenExpiredException);
  }

  public static CommonException buildCommonException(
      String message, WithHttpStatusCode commonMessage) {
    return buildCommonException(message, commonMessage, null);
  }

  public static CommonException buildCommonException(
      String message, WithHttpStatusCode errorMessage, Throwable throwable) {
    return new CommonException(message, errorMessage, throwable);
  }
}
