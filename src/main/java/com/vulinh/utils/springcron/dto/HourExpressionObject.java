package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.HourExpression;
import java.util.List;

public record HourExpressionObject(HourExpression expression, List<Integer> arguments)
    implements ExpressionObject {

  public static ExpressionObject of(HourExpression expression, List<Integer> arguments) {
    return new HourExpressionObject(expression, arguments);
  }

  public static ExpressionObject of(HourExpression expression, int... arguments) {
    return new HourExpressionObject(expression, ExpressionObject.box(arguments));
  }

  public static ExpressionObject defaultObject() {
    return HourExpressionObject.of(null);
  }

  public HourExpressionObject {
    expression = ExpressionObject.defaultIfNull(expression, HourExpression::defaultExpression);
    arguments = ExpressionObject.emptyIfNull(arguments);
  }
}
