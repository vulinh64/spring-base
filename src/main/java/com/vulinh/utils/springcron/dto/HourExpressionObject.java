package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.HourExpression;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record HourExpressionObject(HourExpression expression, List<Integer> arguments)
    implements ExpressionObject {

  public static HourExpressionObject defaultObject() {
    return new HourExpressionObject(null, null);
  }

  public HourExpressionObject {
    expression = Optional.ofNullable(expression).orElse(HourExpression.EVERY_HOUR);
    arguments = Optional.ofNullable(arguments).orElseGet(Collections::emptyList);
  }
}
