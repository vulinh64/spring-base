package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.SecondExpression;
import java.util.List;

public record SecondExpressionObject(SecondExpression expression, List<Integer> arguments)
    implements ExpressionObject {

  public static ExpressionObject of(SecondExpression expression, List<Integer> arguments) {
    return new SecondExpressionObject(expression, arguments);
  }

  public static ExpressionObject of(SecondExpression expression, int... arguments) {
    return new SecondExpressionObject(expression, ExpressionObject.box(arguments));
  }

  public static ExpressionObject defaultObject() {
    return SecondExpressionObject.of(null);
  }

  public SecondExpressionObject {
    expression = ExpressionObject.defaultIfNull(expression, SecondExpression::defaultExpression);
    arguments = ExpressionObject.emptyIfNull(arguments);
  }
}
