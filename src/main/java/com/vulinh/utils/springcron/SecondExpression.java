package com.vulinh.utils.springcron;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SecondExpression implements TimeExpression {
  EVERY_SECOND(List::isEmpty, Constant.ALL),
  EVERY_SECOND_INTERVAL(
      list -> Utils.isUnaryWithinBounds(list, Constant.ZERO, Constant.MAX_SECOND_MINUTE),
      ExpressionUtils::timeEveryInterval),
  EVERY_SECOND_BETWEEN(
      list ->
          Utils.isBinaryList(list)
              && Utils.isBetweenInclusive(list.getFirst(), Constant.ZERO, Utils.secondElement(list))
              && Utils.isBetweenInclusive(
                  Utils.secondElement(list), Constant.ZERO, Constant.MAX_SECOND_MINUTE),
      ExpressionUtils::everyBetween),
  SPECIFIC_SECONDS(
      list -> Utils.isValidListWithinBounds(list, Constant.ZERO, Constant.MAX_SECOND_MINUTE),
      ExpressionUtils::separateByComma);

  private final Predicate<List<Integer>> validator;
  private final Function<List<Integer>, String> generator;
}
