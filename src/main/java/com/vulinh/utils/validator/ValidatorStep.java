package com.vulinh.utils.validator;

import com.vulinh.data.dto.message.WithHttpStatusCode;

import java.util.function.Predicate;

public interface ValidatorStep<T> {

  Predicate<T> getPredicate();

  WithHttpStatusCode getErrorMessage();

  String getAdditionalMessage();
}
