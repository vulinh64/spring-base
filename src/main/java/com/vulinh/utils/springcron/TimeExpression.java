package com.vulinh.utils.springcron;

public interface TimeExpression extends PartExpression {

  static PartExpression timeNoCare() {
    return (NoCarePartExpression) () -> ignored -> "0";
  }
}
