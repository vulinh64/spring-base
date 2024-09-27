package com.vulinh.utils.springcron;

interface TimeExpression extends PartExpression {

  static PartExpression timeNoCare() {
    return (NoCarePartExpression) () -> ignored -> "0";
  }
}
