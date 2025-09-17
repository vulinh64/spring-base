package com.vulinh.utils.springcron;

import module java.base;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MinuteExpressionTest {

  @Test
  void testEveryMinute() {
    var expected = "*";
    var actual = MinuteExpression.EVERY_MINUTE.generateExpression();
    assertEquals(expected, actual);
  }

  // Not empty list
  @Test
  void testEveryMinuteThrows01() {
    assertThrows(
        IllegalArgumentException.class, () -> MinuteExpression.EVERY_MINUTE.generateExpression(1));
  }

  @Test
  void testEveryMinuteInterval() {
    var expected = "0/4";
    var actual = MinuteExpression.EVERY_MINUTE_INTERVAL.generateExpression(4);
    assertEquals(expected, actual);
  }

  // Too many arguments
  @Test
  void testEveryMinuteIntervalThrows01() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MinuteExpression.EVERY_MINUTE_INTERVAL.generateExpression(4, 9));
  }

  @Test
  void testEveryMinuteBetween() {
    var expected = "1-4";
    var actual = MinuteExpression.EVERY_MINUTE_BETWEEN.generateExpression(1, 4);
    assertEquals(expected, actual);
  }

  // First value is below 0
  @Test
  void testEveryMinuteBetweenThrow01() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MinuteExpression.EVERY_MINUTE_BETWEEN.generateExpression(-1, 4));
  }

  // Minute value is above 59
  @Test
  void testEveryMinuteBetweenThrow02() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MinuteExpression.EVERY_MINUTE_BETWEEN.generateExpression(3, 65));
  }

  // First value is greater than second value
  @Test
  void testEveryMinuteBetweenThrow03() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MinuteExpression.EVERY_MINUTE_BETWEEN.generateExpression(48, 24));
  }

  // Empty list
  @Test
  void testEveryMinuteBetweenThrow04() {
    assertThrows(
        IllegalArgumentException.class, MinuteExpression.EVERY_MINUTE_BETWEEN::generateExpression);
  }

  // Too many arguments
  @Test
  void testEveryMinuteBetweenThrow05() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MinuteExpression.EVERY_MINUTE_BETWEEN.generateExpression(4, 5, 9));
  }

  @Test
  void testSpecificMinutes() {
    var expected = "1,4,7,2,15,6";
    var actual = MinuteExpression.SPECIFIC_MINUTES.generateExpression(1, 4, 7, 2, 15, 6);
    assertEquals(expected, actual);
  }

  // One element is below 0
  @Test
  void testSpecificMinutesThrows01() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MinuteExpression.SPECIFIC_MINUTES.generateExpression(-1, 4));
  }

  // One element is above 59
  @Test
  void testSpecificMinutesThrows02() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MinuteExpression.SPECIFIC_MINUTES.generateExpression(8, 60));
  }

  // One element is null
  @Test
  @SuppressWarnings("java:S5778")
  void testSpecificMinutesThrows03() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MinuteExpression.SPECIFIC_MINUTES.generateExpression(Arrays.asList(4, 9, 3, null)));
  }

  // Empty list
  @Test
  void testSpecificMinutesThrows04() {
    assertThrows(
        IllegalArgumentException.class, MinuteExpression.SPECIFIC_MINUTES::generateExpression);
  }

  @Test
  void testEveryMinuteIntervalBetween() {
    var expected = "12-24/2";
    var actual = MinuteExpression.EVERY_MINUTE_INTERVAL_BETWEEN.generateExpression(12, 24, 2);
    assertEquals(expected, actual);
  }

  // Empty list
  @Test
  void testEveryMinuteIntervalBetweenThrows01() {
    assertThrows(
        IllegalArgumentException.class,
        MinuteExpression.EVERY_MINUTE_INTERVAL_BETWEEN::generateExpression);
  }

  // First argument is less than 0
  @Test
  void testEveryMinuteIntervalBetweenThrows02() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MinuteExpression.EVERY_MINUTE_INTERVAL_BETWEEN.generateExpression(-1, 12, 2));
  }

  // Minute argument is less than 0
  @Test
  void testEveryMinuteIntervalBetweenThrows03() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MinuteExpression.EVERY_MINUTE_INTERVAL_BETWEEN.generateExpression(1, -12, 2));
  }

  // Third argument is less than 0
  @Test
  void testEveryMinuteIntervalBetweenThrows04() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MinuteExpression.EVERY_MINUTE_INTERVAL_BETWEEN.generateExpression(1, 12, -1));
  }

  // First argument is larger than 59
  @Test
  void testEveryMinuteIntervalBetweenThrows05() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MinuteExpression.EVERY_MINUTE_INTERVAL_BETWEEN.generateExpression(60, 60, 2));
  }

  // Minute argument is larger than 59
  @Test
  void testEveryMinuteIntervalBetweenThrows06() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MinuteExpression.EVERY_MINUTE_INTERVAL_BETWEEN.generateExpression(1, 60, 2));
  }

  // Third argument is larger than 59
  @Test
  void testEveryMinuteIntervalBetweenThrows07() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MinuteExpression.EVERY_MINUTE_INTERVAL_BETWEEN.generateExpression(1, 12, 60));
  }

  // Minute argument is smaller than first argument
  @Test
  void testEveryMinuteIntervalBetweenThrows08() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MinuteExpression.EVERY_MINUTE_INTERVAL_BETWEEN.generateExpression(35, 27, 1));
  }

  // Too few arguments
  @Test
  void testEveryMinuteIntervalBetweenThrows09() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MinuteExpression.EVERY_MINUTE_INTERVAL_BETWEEN.generateExpression(1, 4));
  }

  // Too many arguments
  @Test
  void testEveryMinuteIntervalBetweenThrows10() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MinuteExpression.EVERY_MINUTE_INTERVAL_BETWEEN.generateExpression(1, 4, 8, 12));
  }
}
