package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.MonthExpression;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record MonthExpressionObject(MonthExpression expression, List<Integer> arguments)
    implements ExpressionObject {

  public static MonthExpressionObject defaultObject() {
    return new MonthExpressionObject(null, null);
  }

  public MonthExpressionObject {
    expression = Optional.ofNullable(expression).orElse(MonthExpression.EVERY_MONTH);
    arguments = Optional.ofNullable(arguments).orElseGet(Collections::emptyList);
  }
}
