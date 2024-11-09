package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.DayExpression;
import java.util.List;

public record DayExpressionObject(DayExpression expression, List<Integer> arguments)
    implements ExpressionObject<DayExpression> {

  public static ExpressionObject<DayExpression> of(
      DayExpression expression, List<Integer> arguments) {
    return new DayExpressionObject(expression, arguments);
  }

  public static ExpressionObject<DayExpression> of(DayExpression expression, int... arguments) {
    return new DayExpressionObject(expression, ExpressionObject.box(arguments));
  }

  public static ExpressionObject<DayExpression> defaultObject() {
    return DayExpressionObject.of(null);
  }

  public DayExpressionObject {
    expression = ExpressionObject.defaultIfNull(expression, DayExpression::defaultExpression);
    arguments = ExpressionObject.emptyIfNull(arguments);
  }
}
