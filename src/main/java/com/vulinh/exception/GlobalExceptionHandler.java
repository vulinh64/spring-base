package com.vulinh.exception;

import module java.base;

import com.vulinh.data.dto.GenericResponse;
import com.vulinh.locale.LocalizationSupport;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.utils.validator.ApplicationError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends CommonExceptionHandler {

  @ExceptionHandler(AuthorizationException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  GenericResponse<Object> handleAuthorizationException(
      AuthorizationException authorizationException) {
    log.info(authorizationException.getMessage());

    return GenericResponse.toError(authorizationException);
  }

  @ExceptionHandler(NoSuchPermissionException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  GenericResponse<Object> handleNoSuchPermissionException(
      NoSuchPermissionException noSuchPermissionException) {
    return logAndReturn(noSuchPermissionException);
  }

  @ExceptionHandler(NotFound404Exception.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  GenericResponse<Object> handleNotFoundException(NotFound404Exception notFound404Exception) {
    return logAndReturn(notFound404Exception);
  }

  @ExceptionHandler(XSSViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  GenericResponse<Object> handleXSSViolationException(XSSViolationException xssViolationException) {
    log.info(
        "XSS violation detected in field [{}]. Offending content: [{}]. Sanitized: [{}]",
        xssViolationException.getFieldName(),
        xssViolationException.getOffendingContent(),
        xssViolationException.getSanitizedContent());

    return GenericResponse.toError(xssViolationException);
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
    // A record's compact constructor runs during Jackson deserialization, so any exception
    // it throws (e.g. XSSViolationException) gets wrapped in JsonMappingException and then
    // in HttpMessageNotReadableException. Walk the cause chain to surface the real error.
    var cause = httpMessageConversionException.getCause();
    var depth = 0;

    while (cause != null && depth < 10) {
      if (cause instanceof XSSViolationException xssViolation) {
        return handleXSSViolationException(xssViolation);
      }

      cause = cause.getCause();
      depth++;
    }

    log.info("Bad request body format: {}", httpMessageConversionException.getMessage());

    return badRequestBody(httpMessageConversionException.getMessage());
  }

  @ExceptionHandler(TypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  GenericResponse<Object> handleTypeMismatchException(TypeMismatchException typeMismatchException) {
    log.atInfo()
        .setMessage("Bad request format: {} (Property name: {}, required type: {}, value: {})")
        .addArgument(typeMismatchException.getMessage())
        .addArgument(typeMismatchException.getPropertyName())
        .addArgument(typeMismatchException.getRequiredType())
        .addArgument(
            () -> StringUtils.abbreviate(String.valueOf(typeMismatchException.getValue()), 100))
        .log();

    return badRequestBody(
        "field [%s] - type [%s]"
            .formatted(
                typeMismatchException.getPropertyName(),
                Optional.ofNullable(typeMismatchException.getRequiredType())
                    .map(Class::getName)
                    .orElse("unknown or empty type")));
  }

  @ExceptionHandler(AuthenticationServiceException.class)
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  GenericResponse<Object> handleAuthenticationServiceException(
      AuthenticationServiceException authenticationServiceException) {
    return securityError(
        ServiceErrorCode.MESSAGE_AUTH_SERVICE_UNAVAILABLE, authenticationServiceException);
  }

  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  GenericResponse<Object> handleInvalidBearerTokenException(
      AuthenticationException authenticationException) {
    return securityError(ServiceErrorCode.MESSAGE_INVALID_AUTHENTICATION, authenticationException);
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  GenericResponse<Object> handleAccessDeniedException(AccessDeniedException accessDeniedException) {
    return securityError(ServiceErrorCode.MESSAGE_INSUFFICIENT_PERMISSION, accessDeniedException);
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

    return GenericResponse.toError(applicationException);
  }

  static GenericResponse<Object> securityError(
      ApplicationError applicationError, Throwable throwable, Object... args) {
    log.info("{} ({})", throwable.getMessage(), throwable.getClass().getName());

    return GenericResponse.builder()
        .errorCode(applicationError)
        .displayMessage(LocalizationSupport.getParsedMessage(applicationError, args))
        .build();
  }
}
