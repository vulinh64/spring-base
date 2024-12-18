package com.vulinh.factory;

import com.vulinh.constant.CommonConstant;
import com.vulinh.data.dto.message.WithHttpStatusCode;
import com.vulinh.exception.CommonException;
import com.vulinh.locale.CommonMessage;
import java.util.UUID;

@SuppressWarnings("java:S6548")
public enum ExceptionFactory {
  INSTANCE;

  public CommonException postNotFound(UUID id) {
    return entityNotFound("Post ID %s not found".formatted(id), CommonConstant.POST_ENTITY);
  }

  public CommonException commentNotFound(UUID postId, UUID commentId) {
    return entityNotFound(
        "Comment ID %s in post %s not found".formatted(commentId, postId),
        CommonConstant.COMMENT_ENTITY);
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

  public CommonException expiredAccessToken(Throwable tokenExpiredException) {
    return buildCommonException(
        "Access token expired", CommonMessage.MESSAGE_CREDENTIALS_EXPIRED, tokenExpiredException);
  }

  public CommonException buildCommonException(String message, WithHttpStatusCode errorMessage) {
    return new CommonException(message, errorMessage, null);
  }

  public CommonException buildCommonException(
      String message, WithHttpStatusCode errorMessage, Throwable throwable) {
    return new CommonException(message, errorMessage, throwable);
  }

  public CommonException buildCommonException(
      String message, WithHttpStatusCode errorMessage, Object... arguments) {
    return new CommonException(message, errorMessage, null, arguments);
  }
}
