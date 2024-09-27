package com.vulinh.utils.springcron.dto;

import java.util.stream.Stream;
import lombok.Builder;

@Builder
public record SpringCronGeneratorDTO(
    SecondExpressionObject second,
    MinuteExpressionObject minute,
    HourExpressionObject hour,
    DayExpressionObject day,
    MonthExpressionObject month,
    DayOfWeekExpressionObject dayOfWeek) {

  public static SpringCronGeneratorDTO of(
      SecondExpressionObject second,
      MinuteExpressionObject minute,
      HourExpressionObject hour,
      DayExpressionObject dayOfWeek,
      MonthExpressionObject month,
      DayOfWeekExpressionObject dayOfMonth) {
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
            Stream.of(second, minute, hour, day, month, dayOfWeek)
                .map(e -> e.expression().generateExpression(e.arguments()))
                .toArray(Object[]::new));
  }
}
