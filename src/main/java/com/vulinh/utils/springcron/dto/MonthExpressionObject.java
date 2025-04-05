package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.MonthExpression;
import java.util.List;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record MonthExpressionObject(MonthExpression expression, List<Integer> arguments)
    implements ExpressionObject {

  public static ExpressionObject of(MonthExpression expression, List<Integer> arguments) {
    return new MonthExpressionObject(expression, arguments);
  }

  public static ExpressionObject of(MonthExpression expression, int... arguments) {
    return new MonthExpressionObject(expression, ExpressionObject.box(arguments));
  }

  public static ExpressionObject defaultObject() {
    return MonthExpressionObject.of(null);
  }

  public MonthExpressionObject {
    expression = ExpressionObject.defaultIfNull(expression, MonthExpression::defaultExpression);
    arguments = ExpressionObject.emptyIfNull(arguments);
  }
}
