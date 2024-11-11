package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.MonthExpression;
import java.util.List;

public record MonthExpressionObject(MonthExpression expression, List<Integer> arguments)
    implements ExpressionObject<MonthExpression> {

  public static ExpressionObject<MonthExpression> of(
      MonthExpression expression, List<Integer> arguments) {
    return new MonthExpressionObject(expression, arguments);
  }

  public static ExpressionObject<MonthExpression> of(MonthExpression expression, int... arguments) {
    return new MonthExpressionObject(expression, ExpressionObject.box(arguments));
  }

  public static ExpressionObject<MonthExpression> defaultObject() {
    return MonthExpressionObject.of(null);
  }

  public MonthExpressionObject {
    expression = ExpressionObject.defaultIfNull(expression, MonthExpression::defaultExpression);
    arguments = ExpressionObject.emptyIfNull(arguments);
  }
}
