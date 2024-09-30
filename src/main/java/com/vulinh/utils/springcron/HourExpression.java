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
              && Utils.isFirstTwoArgumentsCorrect(list, Constant.ZERO, Constant.MAX_HOUR),
      ExpressionUtils::everyBetween),
  SPECIFIC_HOURS(
      list -> Utils.isValidListWithinBounds(list, Constant.ZERO, Constant.MAX_HOUR),
      ExpressionUtils::separateByComma),
  EVERY_HOUR_INTERVAL_BETWEEN(
      list ->
          Utils.isTriList(list)
              && Utils.isFirstTwoArgumentsCorrect(list, Constant.ZERO, Constant.MAX_HOUR)
              && Utils.isBetweenInclusive(
                  Utils.thirdElement(list), Constant.MIN_INTERVAL, Constant.MAX_HOUR),
      list -> "%s/%s".formatted(ExpressionUtils.everyBetween(list), Utils.thirdElement(list)));

  private final Predicate<List<Integer>> validator;
  private final Function<List<Integer>, String> generator;
}
