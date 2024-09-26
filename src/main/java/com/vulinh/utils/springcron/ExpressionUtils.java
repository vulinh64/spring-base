package com.vulinh.utils.springcron;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpressionUtils {

  static final Map<Integer, String> MONTHS_MAPPING =
      Arrays.stream(Month.values())
          .collect(Collectors.toMap(Month::getValue, m -> firstThreeChars(m.name())));

  static final Map<Integer, String> WEEK_DAYS_MAPPING =
      ImmutableMap.<Integer, String>builder()
          .putAll(
              Arrays.stream(DayOfWeek.values())
                  .collect(
                      Collectors.toMap(
                          k -> {
                            if (k == DayOfWeek.SUNDAY) {
                              return 0;
                            }

                            return k.getValue();
                          },
                          m -> firstThreeChars(m.name()))))
          .put(7, firstThreeChars(DayOfWeek.SUNDAY.name()))
          .build();

  static String firstThreeChars(String name) {
    return name.substring(0, 3);
  }

  public static void main(String[] args) {
    System.out.println(WEEK_DAYS_MAPPING);
  }

  static String timeEveryInterval(List<Integer> list) {
    return "0/%s".formatted(list.getFirst());
  }

  static String dateEveryInterval(List<Integer> list) {
    return "*/%s".formatted(list.getFirst());
  }

  static String everyBetween(List<Integer> list) {
    return everyBetween(list, String::valueOf);
  }

  static String everyBetween(List<Integer> list, IntFunction<String> converter) {
    return "%s-%s"
        .formatted(converter.apply(list.getFirst()), converter.apply(secondElement(list)));
  }

  static String separateByComma(List<Integer> list) {
    return separateByComma(list, String::valueOf);
  }

  static String separateByComma(List<Integer> list, IntFunction<String> converter) {
    return list.stream().mapToInt(a -> a).mapToObj(converter).collect(Collectors.joining(","));
  }

  static int secondElement(List<Integer> list) {
    return list.get(1);
  }
}
