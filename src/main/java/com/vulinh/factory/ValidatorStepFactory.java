package com.vulinh.factory;

import module java.base;

import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.utils.validator.ValidatorStep;
import com.vulinh.utils.validator.ValidatorStepImpl;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("java:S6548")
public enum ValidatorStepFactory {
  INSTANCE;

  public static <T> Predicate<T> noBlankField(Function<T, String> extractor) {
    return dto -> StringUtils.isNotBlank(extractor.apply(dto));
  }

  public static <T> Predicate<T> noExceededLength(Function<T, String> extractor, int length) {
    return dto -> StringUtils.length(extractor.apply(dto)) <= length;
  }

  public static <T> Predicate<T> atLeastLength(Function<T, String> extractor, int length) {
    return dto -> StringUtils.length(extractor.apply(dto)) >= length;
  }

  public <T> ValidatorStep<T> build(
      Predicate<T> predicate,
      ServiceErrorCode serviceErrorCode,
      String additionalMessage,
      Object... args) {
    return new ValidatorStepImpl<>(predicate, serviceErrorCode, additionalMessage, args);
  }
}
