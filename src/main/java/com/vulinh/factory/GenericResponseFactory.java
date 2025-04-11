package com.vulinh.factory;

import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.base.I18NCapable;
import com.vulinh.exception.CommonException;
import com.vulinh.locale.CommonMessage;
import org.springframework.lang.Nullable;

@SuppressWarnings("java:S6548")
public enum GenericResponseFactory {
  INSTANCE;

  public <T> GenericResponse<T> success() {
    return createResponse(CommonMessage.MESSAGE_SUCCESS, null);
  }

  public <T> GenericResponse<T> success(T data) {
    return createResponse(CommonMessage.MESSAGE_SUCCESS, data);
  }

  public <T> GenericResponse<T> toGenericResponse(I18NCapable i18NKey, Object... args) {
    return createResponse(i18NKey, null, args);
  }

  public <T> GenericResponse<T> toExceptionResponse(
      CommonException commonException, Object... args) {
    return createResponse(commonException.getErrorKey(), null, args);
  }

  private static <T> GenericResponse<T> createResponse(
      I18NCapable i18NCapable, @Nullable T data, @Nullable Object... args) {
    return GenericResponse.<T>builder()
        .data(data)
        .errorCode(i18NCapable.getErrorCode())
        .displayMessage(i18NCapable.getDisplayMessage(args))
        .build();
  }
}
