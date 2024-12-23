package com.vulinh.utils.springcron;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DayExpression implements PartExpression {
  EVERY_DAY(List::isEmpty, Constant.ALL),
  EVERY_DAY_INTERVAL(
      list -> Utils.isUnaryWithinBounds(list, Constant.MIN_DAY_MONTH_DAY_OF_WEEK, Constant.MAX_DAY),
      ExpressionUtils::dateEveryInterval),
  EVERY_DAYS_BETWEEN(
      list ->
          Utils.isBinaryList(list)
              && Utils.isFirstTwoArgumentsCorrect(
                  list, Constant.MIN_DAY_MONTH_DAY_OF_WEEK, Constant.MAX_DAY),
      ExpressionUtils::everyBetween),
  SPECIFIC_DAYS(
      list ->
          Utils.isValidListWithinBounds(list, Constant.MIN_DAY_MONTH_DAY_OF_WEEK, Constant.MAX_DAY),
      ExpressionUtils::separateByComma),
  BETWEEN_MULTIPLE_RANGES(
      list ->
          Utils.isIncrementalPairInList(list)
              && Utils.isValidListWithinBounds(
                  list, Constant.MIN_DAY_MONTH_DAY_OF_WEEK, Constant.MAX_DAY),
      list ->
          IntStream.range(0, list.size())
              .filter(index -> index % 2 == 0)
              .mapToObj(index -> "%d-%d".formatted(list.getFirst(), list.get(index + 1)))
              .collect(Collectors.joining(",")));

  private final Predicate<List<Integer>> validator;
  private final Function<List<Integer>, String> generator;

  public static DayExpression defaultExpression() {
    return EVERY_DAY;
  }
}
