package com.vulinh.utils.springcron;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MinuteExpression implements TimeExpression {
  EVERY_MINUTE(List::isEmpty, Constant.ALL),
  EVERY_MINUTE_INTERVAL(
      list -> Utils.isUnaryWithinBounds(list, Constant.ZERO, Constant.MAX_SECOND_MINUTE),
      ExpressionUtils::timeEveryInterval),
  EVERY_MINUTE_BETWEEN(
      list ->
          Utils.isBinaryList(list)
              && Utils.isBetweenInclusive(list.getFirst(), Constant.ZERO, Utils.secondElement(list))
              && Utils.isBetweenInclusive(
                  Utils.secondElement(list), Constant.ZERO, Constant.MAX_SECOND_MINUTE),
      ExpressionUtils::everyBetween),
  SPECIFIC_MINUTES(
      list -> Utils.isValidListWithinBounds(list, Constant.ZERO, Constant.MAX_SECOND_MINUTE),
      ExpressionUtils::separateByComma);

  private final Predicate<List<Integer>> validator;
  private final Function<List<Integer>, String> generator;
}
