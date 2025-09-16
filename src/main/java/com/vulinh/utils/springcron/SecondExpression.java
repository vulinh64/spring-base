package com.vulinh.utils.springcron;

import module java.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SecondExpression implements PartExpression {
  EVERY_SECOND(List::isEmpty, Constant.ALL),
  EVERY_SECOND_INTERVAL(
      list -> Utils.isUnaryWithinBounds(list, Constant.ZERO, Constant.MAX_SECOND_MINUTE),
      ExpressionUtils::timeEveryInterval),
  EVERY_SECOND_BETWEEN(
      list ->
          Utils.isBinaryList(list)
              && Utils.isFirstTwoArgumentsCorrect(list, Constant.ZERO, Constant.MAX_SECOND_MINUTE),
      ExpressionUtils::everyBetween),
  SPECIFIC_SECONDS(
      list -> Utils.isValidListWithinBounds(list, Constant.ZERO, Constant.MAX_SECOND_MINUTE),
      ExpressionUtils::separateByComma),
  EVERY_SECOND_INTERVAL_BETWEEN(
      list ->
          Utils.isTriList(list)
              && Utils.isFirstTwoArgumentsCorrect(list, Constant.ZERO, Constant.MAX_SECOND_MINUTE)
              && Utils.isBetweenInclusive(
                  Utils.thirdElement(list), Constant.MIN_INTERVAL, Constant.MAX_SECOND_MINUTE),
      list -> "%s/%s".formatted(ExpressionUtils.everyBetween(list), Utils.thirdElement(list)));

  private final Predicate<List<Integer>> validator;
  private final Function<List<Integer>, String> generator;

  public static SecondExpression defaultExpression() {
    return EVERY_SECOND;
  }
}
