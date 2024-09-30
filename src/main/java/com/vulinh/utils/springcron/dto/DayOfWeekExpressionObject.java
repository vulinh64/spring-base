package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.DayOfWeekExpression;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Builder;

@Builder
public record DayOfWeekExpressionObject(DayOfWeekExpression expression, List<Integer> arguments)
    implements ExpressionObject {

  public static DayOfWeekExpressionObject of(
      DayOfWeekExpression expression, List<Integer> arguments) {
    return new DayOfWeekExpressionObject(expression, arguments);
  }

  public static DayOfWeekExpressionObject of(DayOfWeekExpression expression, int... arguments) {
    return new DayOfWeekExpressionObject(expression, ExpressionObject.box(arguments));
  }

  public static DayOfWeekExpressionObject defaultObject() {
    return DayOfWeekExpressionObject.of(null);
  }

  public DayOfWeekExpressionObject {
    expression = Optional.ofNullable(expression).orElse(DayOfWeekExpression.EVERY_WEEK_DAY);
    arguments = Optional.ofNullable(arguments).orElseGet(Collections::emptyList);
  }
}
