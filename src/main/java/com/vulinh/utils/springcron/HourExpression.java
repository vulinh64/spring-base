package com.vulinh.utils.springcron;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HourExpression implements TimeExpression {
  EVERY_HOUR(List::isEmpty, Constant.ALL),
  EVERY_HOUR_INTERVAL(
      list -> Utils.isUnaryWithinBounds(list, Constant.ZERO, Constant.MAX_HOUR),
      ExpressionUtils::timeEveryInterval),
  EVERY_HOUR_BETWEEN(
      list ->
          Utils.isBinaryList(list)
              && Utils.isBetweenInclusive(list.getFirst(), Constant.ZERO, Utils.secondElement(list))
              && Utils.isBetweenInclusive(
                  Utils.secondElement(list), Constant.ZERO, Constant.MAX_HOUR),
      ExpressionUtils::everyBetween),
  SPECIFIC_HOURS(
      list -> Utils.isValidListWithinBounds(list, Constant.ZERO, Constant.MAX_HOUR),
      ExpressionUtils::separateByComma);

  private final Predicate<List<Integer>> validator;
  private final Function<List<Integer>, String> generator;
}
