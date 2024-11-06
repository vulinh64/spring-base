package com.vulinh.factory;

import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.message.WithHttpStatusCode;
import com.vulinh.exception.CommonException;

@SuppressWarnings("java:S6548")
public enum ExceptionFactory {
  INSTANCE;

  public CommonException entityNotFound(String message, String entityName) {
    return new CommonException(message, CommonMessage.MESSAGE_INVALID_ENTITY_ID, null, entityName);
  }

  public CommonException invalidAuthorization() {
    return invalidAuthorization(null);
  }

  public CommonException invalidAuthorization(Throwable exception) {
    return buildCommonException(
        "Invalid authorization", CommonMessage.MESSAGE_INVALID_AUTHORIZATION, exception);
  }

  public CommonException parsingPublicKeyError(Throwable throwable) {
    return buildCommonException(
        "Parsing public key error", CommonMessage.MESSAGE_INVALID_PUBLIC_KEY_CONFIG, throwable);
  }

  public CommonException expiredAccessToken(Throwable tokenExpiredException) {
    return buildCommonException(
        "Access token expired", CommonMessage.MESSAGE_CREDENTIALS_EXPIRED, tokenExpiredException);
  }

  public CommonException buildCommonException(String message, WithHttpStatusCode commonMessage) {
    return buildCommonException(message, commonMessage, null);
  }

  public CommonException buildCommonException(
      String message, WithHttpStatusCode errorMessage, Throwable throwable) {
    return new CommonException(message, errorMessage, throwable);
  }
}
