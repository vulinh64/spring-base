package com.vulinh.factory;

import com.vulinh.data.base.I18NCapable;
import com.vulinh.data.constant.CommonConstant;
import com.vulinh.exception.CommonException;
import com.vulinh.locale.CommonMessage;
import java.util.UUID;

@SuppressWarnings("java:S6548")
public enum ExceptionFactory {
  INSTANCE;

  public CommonException postNotFound(UUID id) {
    return entityNotFound("Post ID %s not found".formatted(id), CommonConstant.POST_ENTITY);
  }

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

  public CommonException buildCommonException(String message, I18NCapable errorMessage) {
    return new CommonException(message, errorMessage, null);
  }

  public CommonException buildCommonException(
      String message, I18NCapable errorMessage, Throwable throwable) {
    return new CommonException(message, errorMessage, throwable);
  }

  public CommonException buildCommonException(
      String message, I18NCapable errorMessage, Object... arguments) {
    return new CommonException(message, errorMessage, null, arguments);
  }
}
