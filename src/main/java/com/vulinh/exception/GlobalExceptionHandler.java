package com.vulinh.exception;

import module java.base;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.dto.response.GenericResponse.ResponseCreator;
import com.vulinh.locale.LocalizationSupport;
import com.vulinh.locale.ServiceErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  GenericResponse<Object> handleRuntimeErrorException(RuntimeException runtimeException) {
    log.error("Internal server error", runtimeException);

    return GenericResponse.builder()
        .errorCode(ServiceErrorCode.MESSAGE_INTERNAL_ERROR.getErrorCode())
        .displayMessage(
            LocalizationSupport.getParsedMessage(ServiceErrorCode.MESSAGE_INTERNAL_ERROR))
        .build();
  }

  //
  // Basic handler, may not actually be used
  //

  @ExceptionHandler(ApplicationException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  GenericResponse<Object> handleApplicationException(ApplicationException applicationException) {
    return stackTraceAndReturn("Application error", applicationException);
  }

  @ExceptionHandler(AuthorizationException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  GenericResponse<Object> handleAuthorizationException(
      AuthorizationException authorizationException) {
    log.info(authorizationException.getMessage());

    return ResponseCreator.toError(authorizationException);
  }

  @ExceptionHandler(SecurityConfigurationException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  GenericResponse<Object> handleConfigurationException(
      SecurityConfigurationException securityConfigurationException) {
    return stackTraceAndReturn("Configuration error", securityConfigurationException);
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  GenericResponse<Object> handleInvalidCredentialsException(
      InvalidCredentialsException invalidCredentialsException) {
    return logAndReturn(invalidCredentialsException);
  }

  @ExceptionHandler(NoSuchPermissionException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  GenericResponse<Object> handleNoSuchPermissionException(
      NoSuchPermissionException noSuchPermissionException) {
    return logAndReturn(noSuchPermissionException);
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  GenericResponse<Object> handleNotFoundException(NotFoundException notFoundException) {
    return logAndReturn(notFoundException);
  }

  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  GenericResponse<Object> handleValidationException(ValidationException validationException) {
    return logAndReturn(validationException);
  }

  @ExceptionHandler(ResourceConflictException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  GenericResponse<Object> handleResourceConflictException(
      ResourceConflictException resourceConflictException) {
    return logAndReturn(resourceConflictException);
  }

  @ExceptionHandler(HttpMessageConversionException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  GenericResponse<Object> handleHttpMessageConversionException(
      HttpMessageConversionException httpMessageConversionException) {
    log.info("Bad request body format: {}", httpMessageConversionException.getMessage());

    return badRequestBody(httpMessageConversionException.getMessage());
  }

  @ExceptionHandler(TypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  GenericResponse<Object> handleTypeMismatchException(TypeMismatchException typeMismatchException) {
    log.info(
        "Bad request format: {} (Property name: {}, required type: {}, value: {})",
        typeMismatchException.getMessage(),
        typeMismatchException.getPropertyName(),
        typeMismatchException.getRequiredType(),
        StringUtils.abbreviate(String.valueOf(typeMismatchException.getValue()), 100));

    return badRequestBody(
        "field [%s] - type [%s]"
            .formatted(
                typeMismatchException.getPropertyName(),
                Optional.ofNullable(typeMismatchException.getRequiredType())
                    .map(Class::getName)
                    .orElse("unknown or empty type")));
  }

  @ExceptionHandler(JWTVerificationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  GenericResponse<Object> handleJWTVerificationException(
      JWTVerificationException jwtVerificationException) {
    log.info("Invalid JWT token: {}", jwtVerificationException.getMessage());

    return ResponseCreator.toError(AuthorizationException.invalidAuthorization());
  }

  static GenericResponse<Object> badRequestBody(String additionalMessage) {
    return GenericResponse.builder()
        .errorCode(ServiceErrorCode.MESSAGE_INVALID_BODY_REQUEST.getErrorCode())
        .displayMessage(
            LocalizationSupport.getParsedMessage(
                ServiceErrorCode.MESSAGE_INVALID_BODY_REQUEST, additionalMessage))
        .build();
  }

  static GenericResponse<Object> logAndReturn(ApplicationException applicationException) {
    log.info(applicationException.getMessage());

    return ResponseCreator.toError(applicationException);
  }

  static GenericResponse<Object> stackTraceAndReturn(
      String prependMessage, ApplicationException applicationException) {
    log.info(prependMessage, applicationException);

    return ResponseCreator.toError(applicationException);
  }
}
