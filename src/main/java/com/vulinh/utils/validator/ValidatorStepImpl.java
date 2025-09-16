package com.vulinh.utils.validator;

import module java.base;

import com.vulinh.data.base.ApplicationError;
import com.vulinh.locale.ServiceErrorCode;
import org.springframework.lang.NonNull;

public record ValidatorStepImpl<T>(
        Predicate<T> predicate, ServiceErrorCode applicationError, String exceptionMessage, Object[] args)
    implements ValidatorStep<T> {

  public ValidatorStepImpl {
    args = args == null ? new Object[0] : args;
  }

  @Override
  public Predicate<T> getPredicate() {
    return predicate;
  }

  @Override
  public ApplicationError getApplicationError() {
    return applicationError;
  }

  @Override
  public String getExceptionMessage() {
    return exceptionMessage;
  }

  @Override
  public Object[] getArgs() {
    return args;
  }

  @Override
  public boolean equals(Object other) {
    return this == other
        || other
                instanceof
                ValidatorStepImpl<?>(
                    var thePredicate,
                    var theErrorMessage,
                    var theAdditionalMessage,
                    var theArgument)
            && Objects.deepEquals(theArgument, args)
            && Objects.equals(thePredicate, predicate)
            && Objects.equals(theAdditionalMessage, exceptionMessage)
            && theErrorMessage == applicationError;
  }

  @Override
  public int hashCode() {
    return Objects.hash(predicate, applicationError, exceptionMessage, Arrays.hashCode(args));
  }

  @Override
  @NonNull
  public String toString() {
    return "ValidatorStepImpl{predicate=%s, applicationError=%s, exceptionMessage='%s', args=%s}"
        .formatted(predicate, applicationError, exceptionMessage, Arrays.toString(args));
  }
}
