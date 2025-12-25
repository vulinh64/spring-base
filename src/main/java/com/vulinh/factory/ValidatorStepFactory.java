package com.vulinh.factory;

import module java.base;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidatorStepFactory {

  public static <T> Predicate<T> noBlankField(Function<T, String> extractor) {
    return dto -> StringUtils.isNotBlank(extractor.apply(dto));
  }

  public static <T> Predicate<T> noExceededLength(Function<T, String> extractor, int length) {
    return dto -> StringUtils.length(extractor.apply(dto)) <= length;
  }
}
