package com.vulinh.utils.springcron;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DayOfWeekExpression implements DateExpression {
  EVERY_WEEK_DAY(List::isEmpty, Constant.ALL),
  EVERY_WEEK_DAY_INTERVAL(
      list ->
          Utils.isUnaryWithinBounds(
              list, Constant.MIN_DAY_MONTH_DAY_OF_WEEK, Constant.MAX_DAY_OF_WEEK),
      ExpressionUtils::dateEveryInterval),
  EVERY_WEEK_DAYS_BETWEEN(
      list ->
          Utils.isBinaryList(list)
              && Utils.isBetweenInclusive(
                  list.getFirst(), Constant.MIN_DAY_MONTH_DAY_OF_WEEK, Constant.MAX_DAY_OF_WEEK),
      list -> ExpressionUtils.everyBetween(list, ExpressionUtils.MONTHS_MAPPING::get)),
  SPECIFIC_WEEK_DAYS(
      list ->
          Utils.isValidListWithinBounds(
              list, Constant.MIN_DAY_MONTH_DAY_OF_WEEK, Constant.MAX_DAY_OF_WEEK),
      list -> ExpressionUtils.separateByComma(list, ExpressionUtils.WEEK_DAYS_MAPPING::get)),
  NTH_OCCURRENCE(
      list ->
          Utils.isBinaryList(list)
              && Utils.isBetweenInclusive(
                  list.getFirst(), Constant.MIN_DAY_MONTH_DAY_OF_WEEK, Constant.MAX_DAY_OF_WEEK)
              && Utils.isBetweenInclusive(
                  Utils.secondElement(list), Constant.MIN_NTH_WEEK_DAY, Constant.MAX_NTH_WEEK_DAY),
      list ->
          "%s#%s"
              .formatted(
                  ExpressionUtils.WEEK_DAYS_MAPPING.get(list.getFirst()),
                  Utils.secondElement(list)));

  private final Predicate<List<Integer>> validator;
  private final Function<List<Integer>, String> generator;
}
