package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.PartExpression;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface ExpressionObject {

  PartExpression expression();

  List<Integer> arguments();

  static <T> T defaultIfNull(T object, Supplier<T> supplier) {
    return object != null ? object : supplier.get();
  }

  static <T> List<T> emptyIfNull(List<T> list) {
    return defaultIfNull(list, Collections::emptyList);
  }

  static List<Integer> box(int... arguments) {
    return Arrays.stream(arguments).boxed().toList();
  }

  ExpressionObject NO_CARE =
      new ExpressionObject() {

        @Override
        public PartExpression expression() {

          return new PartExpression() {
            @Override
            public Predicate<List<Integer>> getValidator() {
              return List::isEmpty;
            }

            @Override
            public Function<List<Integer>, String> getGenerator() {
              return list -> "0";
            }
          };
        }

        @Override
        public List<Integer> arguments() {
          return Collections.emptyList();
        }
      };
}
