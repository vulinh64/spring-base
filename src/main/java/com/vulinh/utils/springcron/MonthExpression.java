package com.vulinh.utils.springcron;

import module java.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MonthExpression implements PartExpression {
  EVERY_MONTH(List::isEmpty, Constant.ALL),
  EVERY_MONTHS_INTERVAL(
      list ->
          Utils.isUnaryWithinBounds(list, Constant.MIN_DAY_MONTH_DAY_OF_WEEK, Constant.MAX_MONTH),
      ExpressionUtils::dateEveryInterval),
  EVERY_MONTHS_BETWEEN(
      list ->
          Utils.isBinaryList(list)
              && list.stream().allMatch(ExpressionUtils.MONTHS_MAPPING::containsKey),
      list -> ExpressionUtils.everyBetween(list, ExpressionUtils.MONTHS_MAPPING::get)),
  SPECIFIC_MONTHS(
      list ->
          !list.isEmpty()
              && list.stream()
                  .filter(Objects::nonNull)
                  .allMatch(ExpressionUtils.MONTHS_MAPPING::containsKey),
      list -> ExpressionUtils.separateByComma(list, ExpressionUtils.MONTHS_MAPPING::get));

  private final Predicate<List<Integer>> validator;
  private final Function<List<Integer>, String> generator;

  public static MonthExpression defaultExpression() {
    return EVERY_MONTH;
  }
}
