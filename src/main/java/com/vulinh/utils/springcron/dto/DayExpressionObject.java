package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.DayExpression;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Builder;

@Builder
public record DayExpressionObject(DayExpression expression, List<Integer> arguments)
    implements ExpressionObject {

  public static DayExpressionObject of(DayExpression expression, List<Integer> arguments) {
    return new DayExpressionObject(expression, arguments);
  }

  public static DayExpressionObject of(DayExpression expression, int... arguments) {
    return new DayExpressionObject(expression, ExpressionObject.box(arguments));
  }

  public static DayExpressionObject defaultObject() {
    return DayExpressionObject.of(null);
  }

  public DayExpressionObject {
    expression = Optional.ofNullable(expression).orElse(DayExpression.EVERY_DAY);
    arguments = Optional.ofNullable(arguments).orElseGet(Collections::emptyList);
  }
}
