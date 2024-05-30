package com.vulinh.factory;

import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.message.I18NCapable;
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

  private static <T> GenericResponse<T> createResponse(
      I18NCapable i18NCapable, @Nullable T data, @Nullable Object... args) {
    return GenericResponse.<T>builder()
        .data(data)
        .code(i18NCapable.getCode())
        .message(i18NCapable.getMessage(args))
        .build();
  }
}
