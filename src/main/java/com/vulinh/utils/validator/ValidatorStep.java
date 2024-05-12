package com.vulinh.utils.validator;

import com.vulinh.data.dto.message.WithHttpStatusCode;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;
import java.util.function.Predicate;

public interface ValidatorStep<T> {

  Predicate<T> getPredicate();

  WithHttpStatusCode getErrorMessage();

  String getAdditionalMessage();

  static <T> Predicate<T> noBlankField(Function<T, String> extractor) {
    return dto -> StringUtils.isNotBlank(extractor.apply(dto));
  }

  static <T> Predicate<T> noExceededLength(Function<T, String> extractor, int length) {
    return dto -> extractor.apply(dto).length() <= length;
  }

  static <T> Predicate<T> atLeastLength(Function<T, String> extractor, int length) {
    return dto -> extractor.apply(dto).length() >= length;
  }
}
