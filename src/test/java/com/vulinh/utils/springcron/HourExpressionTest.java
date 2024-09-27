package com.vulinh.utils.springcron;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class HourExpressionTest {

  @Test
  void testEveryHour() {
    var expected = "*";
    var actual = HourExpression.EVERY_HOUR.generateExpression();
    assertEquals(expected, actual);
  }

  // Not empty list
  @Test
  void testEveryHourThrows01() {
    assertThrows(
        IllegalArgumentException.class, () -> HourExpression.EVERY_HOUR.generateExpression(1));
  }

  @Test
  void testEveryHourInterval() {
    var expected = "0/4";
    var actual = HourExpression.EVERY_HOUR_INTERVAL.generateExpression(4);
    assertEquals(expected, actual);
  }

  // Too many arguments
  @Test
  void testEveryHourIntervalThrows01() {
    assertThrows(
        IllegalArgumentException.class,
        () -> HourExpression.EVERY_HOUR_INTERVAL.generateExpression(4, 9));
  }

  @Test
  void testEveryHourBetween() {
    var expected = "1-4";
    var actual = HourExpression.EVERY_HOUR_BETWEEN.generateExpression(1, 4);
    assertEquals(expected, actual);
  }

  // First value is below 0
  @Test
  void testEveryHourBetweenThrow01() {
    assertThrows(
        IllegalArgumentException.class,
        () -> HourExpression.EVERY_HOUR_BETWEEN.generateExpression(-1, 4));
  }

  // Hour value is above 26
  @Test
  void testEveryHourBetweenThrow02() {
    assertThrows(
        IllegalArgumentException.class,
        () -> HourExpression.EVERY_HOUR_BETWEEN.generateExpression(3, 26));
  }

  // First value is greater than second value
  @Test
  void testEveryHourBetweenThrow03() {
    assertThrows(
        IllegalArgumentException.class,
        () -> HourExpression.EVERY_HOUR_BETWEEN.generateExpression(16, 12));
  }

  // Empty list
  @Test
  void testEveryHourBetweenThrow04() {
    assertThrows(
        IllegalArgumentException.class, HourExpression.EVERY_HOUR_BETWEEN::generateExpression);
  }

  // Too many arguments
  @Test
  void testEveryHourBetweenThrow05() {
    assertThrows(
        IllegalArgumentException.class,
        () -> HourExpression.EVERY_HOUR_BETWEEN.generateExpression(4, 5, 9));
  }

  @Test
  void testSpecificHours() {
    var expected = "1,4,7,2,15,6";
    var actual = HourExpression.SPECIFIC_HOURS.generateExpression(1, 4, 7, 2, 15, 6);
    assertEquals(expected, actual);
  }

  // One element is below 0
  @Test
  void testSpecificHoursThrows01() {
    assertThrows(
        IllegalArgumentException.class,
        () -> HourExpression.SPECIFIC_HOURS.generateExpression(-1, 4));
  }

  // One element is above 23
  @Test
  void testSpecificHoursThrows02() {
    assertThrows(
        IllegalArgumentException.class,
        () -> HourExpression.SPECIFIC_HOURS.generateExpression(8, 28));
  }

  // One element is null
  @Test
  @SuppressWarnings("java:S5778")
  void testSpecificHoursThrows03() {
    assertThrows(
        IllegalArgumentException.class,
        () -> HourExpression.SPECIFIC_HOURS.generateExpression(Arrays.asList(4, 9, 3, null)));
  }

  // Empty list
  @Test
  void testSpecificHoursThrows04() {
    assertThrows(IllegalArgumentException.class, HourExpression.SPECIFIC_HOURS::generateExpression);
  }

  @Test
  void testEveryHourIntervalBetween() {
    var expected = "12-18/2";
    var actual = HourExpression.EVERY_HOUR_INTERVAL_BETWEEN.generateExpression(12, 18, 2);
    assertEquals(expected, actual);
  }

  // Empty list
  @Test
  void testEveryHourIntervalBetweenThrows01() {
    assertThrows(
        IllegalArgumentException.class,
        HourExpression.EVERY_HOUR_INTERVAL_BETWEEN::generateExpression);
  }

  // First argument is less than 0
  @Test
  void testEveryHourIntervalBetweenThrows02() {
    assertThrows(
        IllegalArgumentException.class,
        () -> HourExpression.EVERY_HOUR_INTERVAL_BETWEEN.generateExpression(-1, 12, 2));
  }

  // Hour argument is less than 0
  @Test
  void testEveryHourIntervalBetweenThrows03() {
    assertThrows(
        IllegalArgumentException.class,
        () -> HourExpression.EVERY_HOUR_INTERVAL_BETWEEN.generateExpression(1, -12, 2));
  }

  // Third argument is less than 0
  @Test
  void testEveryHourIntervalBetweenThrows04() {
    assertThrows(
        IllegalArgumentException.class,
        () -> HourExpression.EVERY_HOUR_INTERVAL_BETWEEN.generateExpression(1, 12, -1));
  }

  // First argument is larger than 23
  @Test
  void testEveryHourIntervalBetweenThrows05() {
    assertThrows(
        IllegalArgumentException.class,
        () -> HourExpression.EVERY_HOUR_INTERVAL_BETWEEN.generateExpression(24, 24, 2));
  }

  // Hour argument is larger than 23
  @Test
  void testEveryHourIntervalBetweenThrows06() {
    assertThrows(
        IllegalArgumentException.class,
        () -> HourExpression.EVERY_HOUR_INTERVAL_BETWEEN.generateExpression(1, 25, 2));
  }

  // Third argument is larger than 23
  @Test
  void testEveryHourIntervalBetweenThrows07() {
    assertThrows(
        IllegalArgumentException.class,
        () -> HourExpression.EVERY_HOUR_INTERVAL_BETWEEN.generateExpression(1, 12, 27));
  }

  // Hour argument is smaller than first argument
  @Test
  void testEveryHourIntervalBetweenThrows08() {
    assertThrows(
        IllegalArgumentException.class,
        () -> HourExpression.EVERY_HOUR_INTERVAL_BETWEEN.generateExpression(12, 8, 1));
  }

  // Too few arguments
  @Test
  void testEveryHourIntervalBetweenThrows09() {
    assertThrows(
        IllegalArgumentException.class,
        () -> HourExpression.EVERY_HOUR_INTERVAL_BETWEEN.generateExpression(1, 4));
  }

  // Too many arguments
  @Test
  void testEveryHourIntervalBetweenThrows10() {
    assertThrows(
        IllegalArgumentException.class,
        () -> HourExpression.EVERY_HOUR_INTERVAL_BETWEEN.generateExpression(1, 4, 8, 12));
  }
}
