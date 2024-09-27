package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.MinuteExpression;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record MinuteExpressionObject(MinuteExpression expression, List<Integer> arguments)
    implements ExpressionObject {

  public static MinuteExpressionObject defaultObject() {
    return new MinuteExpressionObject(null, null);
  }

  public MinuteExpressionObject {
    expression = Optional.ofNullable(expression).orElse(MinuteExpression.EVERY_MINUTE);
    arguments = Optional.ofNullable(arguments).orElseGet(Collections::emptyList);
  }
}
