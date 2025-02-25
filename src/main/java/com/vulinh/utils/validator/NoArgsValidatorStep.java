package com.vulinh.utils.validator;

public interface NoArgsValidatorStep<T> extends ValidatorStep<T> {

  Object[] NO_ARGS = new Object[] {};

  @Override
  default Object[] getArguments() {
    return NO_ARGS;
  }
}
