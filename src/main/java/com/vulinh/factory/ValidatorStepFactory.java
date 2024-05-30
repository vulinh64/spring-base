package com.vulinh.factory;

import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.utils.validator.ValidatorStep;
import com.vulinh.utils.validator.ValidatorStepImpl;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("java:S6548")
public enum ValidatorStepFactory {
  INSTANCE;

  public static <T> Predicate<T> noBlankField(Function<T, String> extractor) {
    return dto -> StringUtils.isNotBlank(extractor.apply(dto));
  }

  public static <T> Predicate<T> noExceededLength(Function<T, String> extractor, int length) {
    return dto -> extractor.apply(dto).length() <= length;
  }

  public static <T> Predicate<T> atLeastLength(Function<T, String> extractor, int length) {
    return dto -> extractor.apply(dto).length() >= length;
  }

  public <T> ValidatorStep<T> build(
      Predicate<T> predicate, CommonMessage commonMessage, String additionalMessage) {
    return new ValidatorStepImpl<>(predicate, commonMessage, additionalMessage);
  }
}
