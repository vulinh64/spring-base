package com.vulinh.utils.springcron;

import java.util.List;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class Constant {

  static final Function<List<Integer>, String> ALL = ignored -> "*";

  static final int ZERO = 0;

  static final int MAX_SECOND_MINUTE = 59;

  static final int MAX_HOUR = 23;

  static final int MIN_DAY_MONTH_DAY_OF_WEEK = 1;

  static final int MAX_DAY = 31;

  static final int MAX_MONTH = 12;

  static final int MAX_DAY_OF_WEEK = 7;

  static final int MIN_NTH_WEEK_DAY = 0;

  static final int MAX_NTH_WEEK_DAY = 5;
}
