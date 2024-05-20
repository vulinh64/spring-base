package com.vulinh.exception;

import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.bundle.CommonMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public GenericResponse<Object> handleRuntimeErrorException(RuntimeException runtimeException) {
    log.error("Internal server error", runtimeException);

    return GenericResponse.builder()
        .code(CommonMessage.MESSAGE_INTERNAL_ERROR.getCode())
        .message(CommonMessage.MESSAGE_INTERNAL_ERROR.getMessage())
        .build();
  }

  @ExceptionHandler(CommonException.class)
  public ResponseEntity<GenericResponse<Object>> handleCommonException(
      CommonException commonException) {
    var errorKey = commonException.getErrorKey();

    if (errorKey.getHttpStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
      log.error("Internal error", commonException);
    } else {
      log.info("Common error", commonException);
    }

    return ResponseEntity.status(errorKey.getHttpStatusCode())
        .body(GenericResponse.toGenericResponse(commonException, commonException.getArgs()));
  }

  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public void handleEntityNotFoundException(EntityNotFoundException entityNotFoundException) {
    log.info("Entity not found", entityNotFoundException);
  }

  @ExceptionHandler(HttpMessageConversionException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public GenericResponse<Object> handleHttpMessageConversionException(
      HttpMessageConversionException httpMessageConversionException) {
    log.info("Bad request body format", httpMessageConversionException);

    return GenericResponse.toGenericResponse(
        CommonMessage.MESSAGE_INVALID_BODY_REQUEST, "Request body");
  }

  @ExceptionHandler(TypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public GenericResponse<Object> handleTypeMismatchException(
      TypeMismatchException typeMismatchException) {
    log.info("Bad request format", typeMismatchException);

    return GenericResponse.toGenericResponse(
        CommonMessage.MESSAGE_INVALID_BODY_REQUEST,
        "field [%s] - type [%s]"
            .formatted(
                typeMismatchException.getPropertyName(),
                Optional.ofNullable(typeMismatchException.getRequiredType())
                    .map(Class::getCanonicalName)
                    .orElse("unknown or empty type")));
  }
}
