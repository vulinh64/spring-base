package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.MonthExpression;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Builder;

@Builder
public record MonthExpressionObject(MonthExpression expression, List<Integer> arguments)
    implements ExpressionObject {

  public static MonthExpressionObject of(MonthExpression expression, List<Integer> arguments) {
    return new MonthExpressionObject(expression, arguments);
  }

  public static MonthExpressionObject of(MonthExpression expression, int... arguments) {
    return new MonthExpressionObject(expression, ExpressionObject.box(arguments));
  }

  public static MonthExpressionObject defaultObject() {
    return MonthExpressionObject.of(null);
  }

  public MonthExpressionObject {
    expression = Optional.ofNullable(expression).orElse(MonthExpression.EVERY_MONTH);
    arguments = Optional.ofNullable(arguments).orElseGet(Collections::emptyList);
  }
}
