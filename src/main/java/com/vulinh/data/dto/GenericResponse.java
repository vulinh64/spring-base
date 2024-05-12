package com.vulinh.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.message.I18NCapable;
import lombok.*;
import org.springframework.lang.Nullable;

@Builder
@With
@JsonInclude(Include.NON_NULL)
public record GenericResponse<T>(String code, String message, T data) {

  public static <T> GenericResponse<T> success() {
    return createResponseBuilder(CommonMessage.MESSAGE_SUCCESS, null);
  }

  public static <T> GenericResponse<T> success(T data) {
    return GenericResponse.createResponseBuilder(CommonMessage.MESSAGE_SUCCESS, data);
  }

  public static GenericResponse<Object> toGenericResponse(I18NCapable i18NKey, Object... args) {
    return createResponseBuilder(i18NKey, null, args);
  }

  private static <T> GenericResponse<T> createResponseBuilder(
      I18NCapable i18NCapable, @Nullable T data, @Nullable Object... args) {
    return GenericResponse.<T>builder()
        .data(data)
        .code(i18NCapable.getCode())
        .message(i18NCapable.getMessage(args))
        .build();
  }
}
