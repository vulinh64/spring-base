package com.vulinh.utils.springcron.dto;

import module java.base;

import com.vulinh.utils.springcron.PartExpression;

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
              return _ -> "0";
            }
          };
        }

        @Override
        public List<Integer> arguments() {
          return Collections.emptyList();
        }
      };
}
