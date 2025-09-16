package com.vulinh.utils.validator;

import module java.base;

import com.vulinh.data.base.ApplicationError;

public interface ValidatorStep<T> {

  Predicate<T> getPredicate();

  ApplicationError getApplicationError();

  String getExceptionMessage();

  Object[] getArgs();
}
