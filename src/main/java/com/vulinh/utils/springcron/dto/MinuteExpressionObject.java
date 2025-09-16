package com.vulinh.utils.springcron.dto;

import module java.base;

import com.vulinh.utils.springcron.MinuteExpression;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record MinuteExpressionObject(MinuteExpression expression, List<Integer> arguments)
    implements ExpressionObject {

  public static ExpressionObject of(MinuteExpression expression, List<Integer> arguments) {
    return new MinuteExpressionObject(expression, arguments);
  }

  public static ExpressionObject of(MinuteExpression expression, int... arguments) {
    return new MinuteExpressionObject(expression, ExpressionObject.box(arguments));
  }

  public static ExpressionObject defaultObject() {
    return MinuteExpressionObject.of(null);
  }

  public MinuteExpressionObject {
    expression = ExpressionObject.defaultIfNull(expression, MinuteExpression::defaultExpression);
    arguments = ExpressionObject.emptyIfNull(arguments);
  }
}
