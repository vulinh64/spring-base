package com.vulinh.utils.validator;

import module java.base;

import com.vulinh.exception.ValidationException;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.lang.NonNull;

public final class ValidatorChain<T> {

  @NonNull final Collection<ValidatorStep<T>> validatorSteps;

  private ValidatorChain() {
    validatorSteps = new ArrayList<>();
  }

  public static <T> ValidatorChain<T> start() {
    return new ValidatorChain<>();
  }

  @SafeVarargs
  public final ValidatorChain<T> addValidator(ValidatorStep<T>... step) {
    if (ArrayUtils.isNotEmpty(step)) {
      validatorSteps.addAll(Arrays.asList(step));
    }

    return this;
  }

  public void executeValidation(T object) {
    if (validatorSteps.isEmpty()) {
      // Nothing to validate
      return;
    }

    for (var validatorStep : validatorSteps) {
      if (!validatorStep.getPredicate().test(object)) {
        throw ValidationException.validationException(
            validatorStep.getExceptionMessage(),
            validatorStep.getApplicationError(),
            validatorStep.getArgs());
      }
    }
  }
}
