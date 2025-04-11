package com.vulinh.utils.validator;

import com.vulinh.data.base.I18NCapable;
import java.util.function.Predicate;

public interface ValidatorStep<T> {

  Predicate<T> getPredicate();

  I18NCapable getError();

  String getExceptionMessage();

  Object[] getArguments();
}
