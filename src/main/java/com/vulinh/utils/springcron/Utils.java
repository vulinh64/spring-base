package com.vulinh.utils.springcron;

import module java.base;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class Utils {

  static boolean isBinaryList(List<Integer> list) {
    return list.size() == 2 && noNullElement(list);
  }

  static boolean isTriList(List<Integer> list) {
    return list.size() == 3 && noNullElement(list);
  }

  static boolean isBetweenInclusive(int value, int lowerBound, int upperBound) {
    return value >= lowerBound && value <= upperBound;
  }

  static boolean isValidListWithinBounds(List<Integer> list, int min, int max) {
    return !list.isEmpty()
        && list.stream().allMatch(e -> e != null && isBetweenInclusive(e, min, max));
  }

  static boolean isUnaryWithinBounds(List<Integer> list, int lowerBound, int upperBound) {
    var first = list.getFirst();

    return list.size() == 1
        && first != null
        && Utils.isBetweenInclusive(first, lowerBound, upperBound);
  }

  static boolean isFirstTwoArgumentsCorrect(List<Integer> list, int lowerBound, int upperBound) {
    var second = Utils.secondElement(list);

    return Utils.isBetweenInclusive(second, lowerBound, upperBound)
        && Utils.isBetweenInclusive(list.getFirst(), lowerBound, second);
  }

  static int secondElement(List<Integer> list) {
    return list.get(1);
  }

  static int thirdElement(List<Integer> list) {
    return list.get(2);
  }

  static boolean isIncrementalPairInList(List<Integer> list) {
    if (list.size() % 2 != 0) {
      return false;
    }

    for (int i = 0; i < list.size() - 1; i += 2) {
      if (list.get(i) > list.get(i + 1)) {
        return false;
      }
    }

    return true;
  }

  static boolean noNullElement(List<Integer> list) {
    return list.stream().noneMatch(Objects::isNull);
  }
}
