package com.vulinh.utils.validator;

import com.vulinh.data.base.ApplicationError;
import java.util.function.Predicate;

public interface ValidatorStep<T> {

  Predicate<T> getPredicate();

  ApplicationError getApplicationError();

  String getExceptionMessage();

  Object[] getArgs();
}
