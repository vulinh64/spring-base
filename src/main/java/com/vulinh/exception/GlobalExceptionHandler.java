package com.vulinh.exception;

import com.vulinh.data.dto.GenericResponse;
import com.vulinh.factory.GenericResponseFactory;
import com.vulinh.locale.CommonMessage;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  private static final GenericResponseFactory RESPONSE_FACTORY = GenericResponseFactory.INSTANCE;

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public GenericResponse<Object> handleRuntimeErrorException(RuntimeException runtimeException) {
    log.error("Internal server error", runtimeException);

    return GenericResponse.builder()
        .code(CommonMessage.MESSAGE_INTERNAL_ERROR.getCode())
        .message(CommonMessage.MESSAGE_INTERNAL_ERROR.getDisplayMessage())
        .build();
  }

  @ExceptionHandler(CommonException.class)
  public ResponseEntity<GenericResponse<Object>> handleCommonException(
      CommonException commonException) {
    var statusCode = commonException.getErrorKey().getHttpStatusCode();

    var errorMessage =
        switch (statusCode) {
          case HttpStatus.BAD_REQUEST -> "Bad request";
          case HttpStatus.UNAUTHORIZED -> "Authentication/Authorization error";
          case HttpStatus.FORBIDDEN -> "Access to the resource denied";
          case HttpStatus.NOT_FOUND -> "Resource not found";
          case HttpStatus.CONFLICT -> "Resource already existed";
          case HttpStatus.INTERNAL_SERVER_ERROR -> "Internal server error";
          default -> "Unknown error";
        };

    if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR) {
      log.error(errorMessage, commonException);
    } else {
      log.info(errorMessage, commonException);
    }

    return ResponseEntity.status(statusCode)
        .body(RESPONSE_FACTORY.toExceptionResponse(commonException, commonException.getArgs()));
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

    return RESPONSE_FACTORY.toGenericResponse(
        CommonMessage.MESSAGE_INVALID_BODY_REQUEST, "Request body");
  }

  @ExceptionHandler(TypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public GenericResponse<Object> handleTypeMismatchException(
      TypeMismatchException typeMismatchException) {
    log.info("Bad request format", typeMismatchException);

    return RESPONSE_FACTORY.toGenericResponse(
        CommonMessage.MESSAGE_INVALID_BODY_REQUEST,
        "field [%s] - type [%s]"
            .formatted(
                typeMismatchException.getPropertyName(),
                Optional.ofNullable(typeMismatchException.getRequiredType())
                    .map(Class::getCanonicalName)
                    .orElse("unknown or empty type")));
  }
}
