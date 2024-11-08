package com.vulinh.utils.springcron;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MinuteExpression implements PartExpression {
  EVERY_MINUTE(List::isEmpty, Constant.ALL),
  EVERY_MINUTE_INTERVAL(
      list -> Utils.isUnaryWithinBounds(list, Constant.ZERO, Constant.MAX_SECOND_MINUTE),
      ExpressionUtils::timeEveryInterval),
  EVERY_MINUTE_BETWEEN(
      list ->
          Utils.isBinaryList(list)
              && Utils.isFirstTwoArgumentsCorrect(list, Constant.ZERO, Constant.MAX_SECOND_MINUTE),
      ExpressionUtils::everyBetween),
  SPECIFIC_MINUTES(
      list -> Utils.isValidListWithinBounds(list, Constant.ZERO, Constant.MAX_SECOND_MINUTE),
      ExpressionUtils::separateByComma),
  EVERY_MINUTE_INTERVAL_BETWEEN(
      list ->
          Utils.isTriList(list)
              && Utils.isFirstTwoArgumentsCorrect(list, Constant.ZERO, Constant.MAX_SECOND_MINUTE)
              && Utils.isBetweenInclusive(
                  Utils.thirdElement(list), Constant.MIN_INTERVAL, Constant.MAX_SECOND_MINUTE),
      list -> "%s/%s".formatted(ExpressionUtils.everyBetween(list), Utils.thirdElement(list)));

  private final Predicate<List<Integer>> validator;
  private final Function<List<Integer>, String> generator;

  public static MinuteExpression defaultExpression() {
    return EVERY_MINUTE;
  }
}
