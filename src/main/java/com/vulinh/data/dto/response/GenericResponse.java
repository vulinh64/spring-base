package com.vulinh.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.vulinh.data.base.ApplicationError;
import com.vulinh.exception.ApplicationException;
import com.vulinh.locale.LocalizationSupport;
import com.vulinh.locale.ServiceErrorCode;
import lombok.*;

@With
@Builder
@JsonInclude(Include.NON_NULL)
public record GenericResponse<T>(String errorCode, String displayMessage, T data) {

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class ResponseCreator {

    public static <T> GenericResponse<T> success(T data) {
      return GenericResponse.<T>builder()
          .errorCode(ServiceErrorCode.MESSAGE_SUCCESS.getErrorCode())
          .displayMessage(LocalizationSupport.getParsedMessage(ServiceErrorCode.MESSAGE_SUCCESS))
          .data(data)
          .build();
    }

    public static <T> GenericResponse<T> success() {
      return toError(ServiceErrorCode.MESSAGE_SUCCESS);
    }

    public static <T> GenericResponse<T> toError(
        ApplicationError applicationError, Object... args) {
      return GenericResponse.<T>builder()
          .errorCode(applicationError.getErrorCode())
          .displayMessage(LocalizationSupport.getParsedMessage(applicationError, args))
          .build();
    }

    public static <T> GenericResponse<T> toError(ApplicationException applicationException) {
      return toError(applicationException.getApplicationError(), applicationException.getArgs());
    }
  }
}
