package com.vulinh.utils.springcron;

import java.util.List;

public interface ExpressionObject {

  PartExpression getPartExpression();

  List<Integer> getArguments();
}
