package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.DayExpression;
import java.util.List;

public record DayExpressionObject(DayExpression expression, List<Integer> arguments)
    implements ExpressionObject {

  public static ExpressionObject of(DayExpression expression, List<Integer> arguments) {
    return new DayExpressionObject(expression, arguments);
  }

  public static ExpressionObject of(DayExpression expression, int... arguments) {
    return new DayExpressionObject(expression, ExpressionObject.box(arguments));
  }

  public static ExpressionObject defaultObject() {
    return DayExpressionObject.of(null);
  }

  public DayExpressionObject {
    expression = ExpressionObject.defaultIfNull(expression, DayExpression::defaultExpression);
    arguments = ExpressionObject.emptyIfNull(arguments);
  }
}
