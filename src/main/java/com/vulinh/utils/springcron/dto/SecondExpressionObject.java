package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.SecondExpression;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Builder;

@Builder
public record SecondExpressionObject(SecondExpression expression, List<Integer> arguments)
    implements ExpressionObject {

  public static SecondExpressionObject of(SecondExpression expression, List<Integer> arguments) {
    return new SecondExpressionObject(expression, arguments);
  }

  public static SecondExpressionObject of(SecondExpression expression, int... arguments) {
    return new SecondExpressionObject(expression, ExpressionObject.box(arguments));
  }

  public static SecondExpressionObject defaultObject() {
    return SecondExpressionObject.of(null);
  }

  public SecondExpressionObject {
    expression = Optional.ofNullable(expression).orElse(SecondExpression.EVERY_SECOND);
    arguments = Optional.ofNullable(arguments).orElseGet(Collections::emptyList);
  }
}
