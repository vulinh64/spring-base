package com.vulinh.utils.springcron;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.vulinh.utils.springcron.dto.*;
import org.junit.jupiter.api.Test;

class GeneralTest {

  @Test
  void test01() {
    var expected = "10-30/2 20-40/5 1-6/2 * JAN-AUG *";
    var actual =
        SpringCronGeneratorDTO.builder()
            .second(
                SecondExpressionObject.of(
                    SecondExpression.EVERY_SECOND_INTERVAL_BETWEEN, 10, 30, 2))
            .minute(
                MinuteExpressionObject.of(
                    MinuteExpression.EVERY_MINUTE_INTERVAL_BETWEEN, 20, 40, 5))
            .hour(HourExpressionObject.of(HourExpression.EVERY_HOUR_INTERVAL_BETWEEN, 1, 6, 2))
            .month(MonthExpressionObject.of(MonthExpression.EVERY_MONTHS_BETWEEN, 1, 8))
            .build()
            .toCronExpression();

    assertEquals(expected, actual);
  }
}
