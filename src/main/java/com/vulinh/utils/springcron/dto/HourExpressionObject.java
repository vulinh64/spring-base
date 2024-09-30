package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.HourExpression;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Builder;

@Builder
public record HourExpressionObject(HourExpression expression, List<Integer> arguments)
    implements ExpressionObject {

  public static HourExpressionObject of(HourExpression expression, List<Integer> arguments) {
    return new HourExpressionObject(expression, arguments);
  }

  public static HourExpressionObject of(HourExpression expression, int... arguments) {
    return new HourExpressionObject(expression, ExpressionObject.box(arguments));
  }

  public static HourExpressionObject defaultObject() {
    return HourExpressionObject.of(null);
  }

  public HourExpressionObject {
    expression = Optional.ofNullable(expression).orElse(HourExpression.EVERY_HOUR);
    arguments = Optional.ofNullable(arguments).orElseGet(Collections::emptyList);
  }
}
