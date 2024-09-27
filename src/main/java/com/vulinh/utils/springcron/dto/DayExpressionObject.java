package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.DayExpression;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record DayExpressionObject(DayExpression expression, List<Integer> arguments)
    implements ExpressionObject {

  public static DayExpressionObject defaultObject() {
    return new DayExpressionObject(null, null);
  }

  public DayExpressionObject {
    expression = Optional.ofNullable(expression).orElse(DayExpression.EVERY_DAY);
    arguments = Optional.ofNullable(arguments).orElseGet(Collections::emptyList);
  }
}
