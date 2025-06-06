package com.vulinh.exception;

import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.dto.response.GenericResponse.ResponseCreator;
import com.vulinh.locale.ServiceErrorCode;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
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
  public GenericResponse<Object> handleRuntimeErrorException(RuntimeException runtimeException) {
    log.error("Internal server error", runtimeException);

    return GenericResponse.builder()
        .errorCode(ServiceErrorCode.MESSAGE_INTERNAL_ERROR.getErrorCode())
        .displayMessage(ServiceErrorCode.MESSAGE_INTERNAL_ERROR.getDisplayMessage())
        .build();
  }

  //
  // Basic handler, may not actually be used
  //

  @ExceptionHandler(ApplicationException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public GenericResponse<Object> handleApplicationException(
      ApplicationException applicationException) {
    return stackTraceAndReturn("Application error", applicationException);
  }

  @ExceptionHandler(AuthorizationException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public GenericResponse<Object> handleAuthorizationException(
      AuthorizationException authorizationException) {
    log.info(authorizationException.getMessage());

    return ResponseCreator.toError(authorizationException);
  }

  @ExceptionHandler(SecurityConfigurationException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public GenericResponse<Object> handleConfigurationException(
      SecurityConfigurationException securityConfigurationException) {
    return stackTraceAndReturn("Configuration error", securityConfigurationException);
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public GenericResponse<Object> handleInvalidCredentialsException(
      InvalidCredentialsException invalidCredentialsException) {
    return logAndReturn(invalidCredentialsException);
  }

  @ExceptionHandler(NoSuchPermissionException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public GenericResponse<Object> handleNoSuchPermissionException(
      NoSuchPermissionException noSuchPermissionException) {
    return logAndReturn(noSuchPermissionException);
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public GenericResponse<Object> handleNotFoundException(NotFoundException notFoundException) {
    return logAndReturn(notFoundException);
  }

  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public GenericResponse<Object> handleValidationException(
      ValidationException validationException) {
    return logAndReturn(validationException);
  }

  @ExceptionHandler(ResourceConflictException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public GenericResponse<Object> handleResourceConflictException(
      ResourceConflictException resourceConflictException) {
    return logAndReturn(resourceConflictException);
  }

  @ExceptionHandler(HttpMessageConversionException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public GenericResponse<Object> handleHttpMessageConversionException(
      HttpMessageConversionException httpMessageConversionException) {
    log.info("Bad request body format", httpMessageConversionException);

    return badRequestBody(httpMessageConversionException.getMessage());
  }

  @ExceptionHandler(TypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public GenericResponse<Object> handleTypeMismatchException(
      TypeMismatchException typeMismatchException) {
    log.info("Bad request format", typeMismatchException);

    return badRequestBody(
        "field [%s] - type [%s]"
            .formatted(
                typeMismatchException.getPropertyName(),
                Optional.ofNullable(typeMismatchException.getRequiredType())
                    .map(Class::getName)
                    .orElse("unknown or empty type")));
  }

  private static GenericResponse<Object> badRequestBody(String additionalMessage) {
    return GenericResponse.builder()
        .errorCode(ServiceErrorCode.MESSAGE_INVALID_BODY_REQUEST.getErrorCode())
        .displayMessage(
            ServiceErrorCode.MESSAGE_INVALID_BODY_REQUEST.getDisplayMessage(additionalMessage))
        .build();
  }

  private static GenericResponse<Object> logAndReturn(ApplicationException applicationException) {
    log.info(applicationException.getMessage());

    return ResponseCreator.toError(applicationException);
  }

  private static GenericResponse<Object> stackTraceAndReturn(
      String prependMessage, ApplicationException applicationException) {
    log.info(prependMessage, applicationException);

    return ResponseCreator.toError(applicationException);
  }
}
