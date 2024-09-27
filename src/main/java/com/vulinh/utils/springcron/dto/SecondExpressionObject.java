package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.SecondExpression;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record SecondExpressionObject(SecondExpression expression, List<Integer> arguments)
    implements ExpressionObject {

  public static SecondExpressionObject defaultObject() {
    return new SecondExpressionObject(null, null);
  }

  public SecondExpressionObject {
    expression = Optional.ofNullable(expression).orElse(SecondExpression.EVERY_SECOND);
    arguments = Optional.ofNullable(arguments).orElseGet(Collections::emptyList);
  }
}
