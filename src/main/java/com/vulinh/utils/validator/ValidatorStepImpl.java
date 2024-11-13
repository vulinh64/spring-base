package com.vulinh.utils.validator;

import com.vulinh.locale.CommonMessage;
import com.vulinh.data.dto.message.WithHttpStatusCode;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public record ValidatorStepImpl<T>(
    Predicate<T> predicate,
    CommonMessage errorMessage,
    String additionalMessage,
    Object[] arguments)
    implements ValidatorStep<T> {

  public ValidatorStepImpl {
    arguments = arguments == null ? new Object[0] : arguments;
  }

  @Override
  public Predicate<T> getPredicate() {
    return predicate;
  }

  @Override
  public WithHttpStatusCode getErrorMessage() {
    return errorMessage;
  }

  @Override
  public String getAdditionalMessage() {
    return additionalMessage;
  }

  @Override
  public Object[] getArguments() {
    return arguments;
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
            && Objects.deepEquals(theArgument, arguments)
            && Objects.equals(thePredicate, predicate)
            && Objects.equals(theAdditionalMessage, additionalMessage)
            && theErrorMessage == errorMessage;
  }

  @Override
  public int hashCode() {
    return Objects.hash(predicate, errorMessage, additionalMessage, Arrays.hashCode(arguments));
  }

  @Override
  public String toString() {
    return "ValidatorStepImpl{predicate=%s, errorMessage=%s, additionalMessage='%s', arguments=%s}"
        .formatted(predicate, errorMessage, additionalMessage, Arrays.toString(arguments));
  }
}
