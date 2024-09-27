package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.DayOfWeekExpression;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record DayOfWeekExpressionObject(DayOfWeekExpression expression, List<Integer> arguments)
    implements ExpressionObject {

  public static DayOfWeekExpressionObject defaultObject() {
    return new DayOfWeekExpressionObject(null, null);
  }

  public DayOfWeekExpressionObject {
    expression = Optional.ofNullable(expression).orElse(DayOfWeekExpression.EVERY_WEEK_DAY);
    arguments = Optional.ofNullable(arguments).orElseGet(Collections::emptyList);
  }
}
