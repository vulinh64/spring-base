package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.DayOfWeekExpression;
import java.util.List;
import lombok.Builder;

@Builder
public record DayOfWeekExpressionObject(DayOfWeekExpression expression, List<Integer> arguments)
    implements ExpressionObject<DayOfWeekExpression> {

  public static ExpressionObject<DayOfWeekExpression> of(
      DayOfWeekExpression expression, List<Integer> arguments) {
    return new DayOfWeekExpressionObject(expression, arguments);
  }

  public static ExpressionObject<DayOfWeekExpression> of(
      DayOfWeekExpression expression, int... arguments) {
    return new DayOfWeekExpressionObject(expression, ExpressionObject.box(arguments));
  }

  public static ExpressionObject<DayOfWeekExpression> defaultObject() {
    return DayOfWeekExpressionObject.of(null);
  }

  public DayOfWeekExpressionObject {
    expression =
        ExpressionObject.defaultIfNull(expression, DayOfWeekExpression::defaultExpression);
    arguments = ExpressionObject.emptyIfNull(arguments);
  }
}
