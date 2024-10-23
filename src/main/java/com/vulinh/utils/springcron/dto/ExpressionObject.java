package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.PartExpression;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public interface ExpressionObject {

  PartExpression expression();

  List<Integer> arguments();

  static <T> T defaultIfNull(T object, Supplier<T> supplier) {
    return object != null ? object : supplier.get();
  }

  static List<Integer> box(int... arguments) {
    return Arrays.stream(arguments).boxed().toList();
  }
}
