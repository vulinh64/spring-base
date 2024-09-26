package com.vulinh.utils.springcron;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public enum DayOfWeekExpression implements DateExpression {
  EVERY_WEEKDAY(List::isEmpty, Constant.ALL);

  private final Predicate<List<Integer>> validator;
  private final Function<List<Integer>, String> generator;
}
