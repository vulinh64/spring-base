package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.MinuteExpression;
import java.util.List;

public record MinuteExpressionObject(MinuteExpression expression, List<Integer> arguments)
    implements ExpressionObject<MinuteExpression> {

  public static ExpressionObject<MinuteExpression> of(
      MinuteExpression expression, List<Integer> arguments) {
    return new MinuteExpressionObject(expression, arguments);
  }

  public static ExpressionObject<MinuteExpression> of(
      MinuteExpression expression, int... arguments) {
    return new MinuteExpressionObject(expression, ExpressionObject.box(arguments));
  }

  public static ExpressionObject<MinuteExpression> defaultObject() {
    return MinuteExpressionObject.of(null);
  }

  public MinuteExpressionObject {
    expression = ExpressionObject.defaultIfNull(expression, MinuteExpression::defaultExpression);
    arguments = ExpressionObject.emptyIfNull(arguments);
  }
}
