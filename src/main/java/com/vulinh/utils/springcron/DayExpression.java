package com.vulinh.utils.springcron;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DayExpression implements DateExpression {
  EVERY_DAY(List::isEmpty, Constant.ALL),
  EVERY_DAY_INTERVAL(
      list -> Utils.isUnaryWithinBounds(list, Constant.MIN_DAY_MONTH_DAY_OF_WEEK, Constant.MAX_DAY),
      ExpressionUtils::dateEveryInterval),
  EVERY_DAYS_BETWEEN(
      list ->
          Utils.isBinaryList(list)
              && Utils.isBetweenInclusive(
                  list.getFirst(), Constant.MIN_DAY_MONTH_DAY_OF_WEEK, Constant.MAX_DAY),
      ExpressionUtils::everyBetween),
  SPECIFIC_DAYS(
      list ->
          Utils.isValidListWithinBounds(
              list, Constant.MIN_DAY_MONTH_DAY_OF_WEEK, Constant.MAX_MONTH),
      list -> ExpressionUtils.separateByComma(list, ExpressionUtils.MONTHS_MAPPING::get));

  private final Predicate<List<Integer>> validator;
  private final Function<List<Integer>, String> generator;
}
