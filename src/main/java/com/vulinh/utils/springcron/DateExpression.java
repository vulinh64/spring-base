package com.vulinh.utils.springcron;

public interface DateExpression extends PartExpression {

  static PartExpression dateNoCare() {
    return (NoCarePartExpression) () -> ignored -> "1";
  }
}
