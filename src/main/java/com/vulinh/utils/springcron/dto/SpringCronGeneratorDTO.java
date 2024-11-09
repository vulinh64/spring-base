package com.vulinh.utils.springcron.dto;

import com.vulinh.utils.springcron.*;
import lombok.Builder;

import java.util.stream.Stream;

@Builder
public record SpringCronGeneratorDTO(
    ExpressionObject<SecondExpression> second,
    ExpressionObject<MinuteExpression> minute,
    ExpressionObject<HourExpression> hour,
    ExpressionObject<DayExpression> day,
    ExpressionObject<MonthExpression> month,
    ExpressionObject<DayOfWeekExpression> dayOfWeek) {

  public static SpringCronGeneratorDTO of(
      ExpressionObject<SecondExpression> second,
      ExpressionObject<MinuteExpression> minute,
      ExpressionObject<HourExpression> hour,
      ExpressionObject<DayExpression> dayOfWeek,
      ExpressionObject<MonthExpression> month,
      ExpressionObject<DayOfWeekExpression> dayOfMonth) {
    return new SpringCronGeneratorDTO(second, minute, hour, dayOfWeek, month, dayOfMonth);
  }

  public static SpringCronGeneratorDTO defaultObject() {
    return new SpringCronGeneratorDTO(null, null, null, null, null, null);
  }

  public SpringCronGeneratorDTO {
    second = ExpressionObject.defaultIfNull(second, SecondExpressionObject::defaultObject);
    minute = ExpressionObject.defaultIfNull(minute, MinuteExpressionObject::defaultObject);
    hour = ExpressionObject.defaultIfNull(hour, HourExpressionObject::defaultObject);
    day = ExpressionObject.defaultIfNull(day, DayExpressionObject::defaultObject);
    month = ExpressionObject.defaultIfNull(month, MonthExpressionObject::defaultObject);
    dayOfWeek = ExpressionObject.defaultIfNull(dayOfWeek, DayOfWeekExpressionObject::defaultObject);
  }

  public String toCronExpression() {
    return "%s %s %s %s %s %s"
        .formatted(
            Stream.<ExpressionObject<?>>of(second, minute, hour, day, month, dayOfWeek)
                .map(e -> e.expression().generateExpression(e.arguments()))
                .toArray(Object[]::new));
  }
}
