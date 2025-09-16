package com.vulinh.utils.springcron;

import module java.base;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

public interface PartExpression {

  Predicate<List<Integer>> getValidator();

  Function<List<Integer>, String> getGenerator();

  default String getRules() {
    return StringUtils.EMPTY;
  }

  default String generateExpression(int... arguments) {
    return generateExpression(Arrays.stream(arguments).boxed().toList());
  }

  default String generateExpression(@Nullable List<Integer> arguments) {
    var nonNullArgument = ListUtils.emptyIfNull(arguments);

    if (nonNullArgument.stream().anyMatch(Objects::isNull)) {
      throw new IllegalArgumentException("List contains one or more null elements");
    }

    if (!getValidator().test(nonNullArgument)) {
      var initialMessage = "Invalid args";
      var rules = getRules();

      var errorMessage =
          StringUtils.isNotBlank(rules)
              ? "%s. Rules:%n%n%s".formatted(initialMessage, rules)
              : initialMessage;

      throw new IllegalArgumentException(errorMessage);
    }

    return getGenerator().apply(nonNullArgument);
  }
}
