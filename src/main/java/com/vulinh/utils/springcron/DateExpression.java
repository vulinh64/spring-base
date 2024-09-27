package com.vulinh.utils.springcron;

interface DateExpression extends PartExpression {

  static PartExpression dateNoCare() {
    return (NoCarePartExpression) () -> ignored -> "1";
  }
}
