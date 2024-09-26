package com.vulinh.utils.springcron;

import java.util.List;
import java.util.Objects;
import java.util.function.IntPredicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class Utils {

  static boolean isBinaryList(List<Integer> list) {
    return list.size() == 2 && list.stream().noneMatch(Objects::isNull);
  }

  static boolean isBetweenInclusive(int value, int lowerBound, int upperBound) {
    return value >= lowerBound && value <= upperBound;
  }

  static boolean isValidListWithinBounds(List<Integer> list, int min, int max) {
    return !list.isEmpty() && list.stream().allMatch(e -> isBetweenInclusive(e, min, max));
  }

  static boolean isValidMatchedItems(List<Integer> list, IntPredicate predicate) {
    return !list.isEmpty() && list.stream().mapToInt(a -> a).allMatch(predicate);
  }

  static boolean isUnaryWithinBounds(List<Integer> list, int lowerBound, int upperBound) {
    return list.size() == 1 && Utils.isBetweenInclusive(list.getFirst(), lowerBound, upperBound);
  }

  static int secondElement(List<Integer> list) {
    return list.get(1);
  }
}
