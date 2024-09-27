package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.PartExpression;

import java.util.List;
import java.util.function.Supplier;

public interface ExpressionObject {

  PartExpression expression();

  List<Integer> arguments();

  static <T> T defaultIfNull(T object, Supplier<T> supplier) {
    if (object == null) {
      return supplier.get();
    }

    return object;
  }
}
