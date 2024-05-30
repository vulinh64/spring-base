package com.vulinh.utils.validator;

import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.message.WithHttpStatusCode;
import java.util.function.Predicate;

public record ValidatorStepImpl<T>(
    Predicate<T> predicate, CommonMessage errorMessage, String additionalMessage)
    implements ValidatorStep<T> {

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
}
