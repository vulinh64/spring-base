package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.MinuteExpression;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Builder;

@Builder
public record MinuteExpressionObject(MinuteExpression expression, List<Integer> arguments)
    implements ExpressionObject {

  public static MinuteExpressionObject of(MinuteExpression expression, List<Integer> arguments) {
    return new MinuteExpressionObject(expression, arguments);
  }

  public static MinuteExpressionObject of(MinuteExpression expression, int... arguments) {
    return new MinuteExpressionObject(expression, ExpressionObject.box(arguments));
  }

  public static MinuteExpressionObject defaultObject() {
    return MinuteExpressionObject.of(null);
  }

  public MinuteExpressionObject {
    expression = Optional.ofNullable(expression).orElse(MinuteExpression.EVERY_MINUTE);
    arguments = Optional.ofNullable(arguments).orElseGet(Collections::emptyList);
  }
}
