package com.vulinh.utils.springcron;

import module java.base;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SecondExpressionTest {

  @Test
  void testEverySecond() {
    var expected = "*";
    var actual = SecondExpression.EVERY_SECOND.generateExpression();
    assertEquals(expected, actual);
  }

  // Not empty list
  @Test
  void testEverySecondThrows01() {
    assertThrows(
        IllegalArgumentException.class, () -> SecondExpression.EVERY_SECOND.generateExpression(1));
  }

  @Test
  void testEverySecondInterval() {
    var expected = "0/4";
    var actual = SecondExpression.EVERY_SECOND_INTERVAL.generateExpression(4);
    assertEquals(expected, actual);
  }

  // Too many arguments
  @Test
  void testEverySecondIntervalThrows01() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SecondExpression.EVERY_SECOND_INTERVAL.generateExpression(4, 9));
  }

  @Test
  void testEverySecondBetween() {
    var expected = "1-4";
    var actual = SecondExpression.EVERY_SECOND_BETWEEN.generateExpression(1, 4);
    assertEquals(expected, actual);
  }

  // First value is below 0
  @Test
  void testEverySecondBetweenThrow01() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SecondExpression.EVERY_SECOND_BETWEEN.generateExpression(-1, 4));
  }

  // Second value is above 59
  @Test
  void testEverySecondBetweenThrow02() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SecondExpression.EVERY_SECOND_BETWEEN.generateExpression(3, 65));
  }

  // First value is greater than second value
  @Test
  void testEverySecondBetweenThrow03() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SecondExpression.EVERY_SECOND_BETWEEN.generateExpression(48, 24));
  }

  // Empty list
  @Test
  void testEverySecondBetweenThrow04() {
    assertThrows(
        IllegalArgumentException.class, SecondExpression.EVERY_SECOND_BETWEEN::generateExpression);
  }

  // Too many arguments
  @Test
  void testEverySecondBetweenThrow05() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SecondExpression.EVERY_SECOND_BETWEEN.generateExpression(4, 5, 9));
  }

  @Test
  void testSpecificSeconds() {
    var expected = "1,4,7,2,15,6";
    var actual = SecondExpression.SPECIFIC_SECONDS.generateExpression(1, 4, 7, 2, 15, 6);
    assertEquals(expected, actual);
  }

  // One element is below 0
  @Test
  void testSpecificSecondsThrows01() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SecondExpression.SPECIFIC_SECONDS.generateExpression(-1, 4));
  }

  // One element is above 59
  @Test
  void testSpecificSecondsThrows02() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SecondExpression.SPECIFIC_SECONDS.generateExpression(8, 60));
  }

  // One element is null
  @Test
  @SuppressWarnings("java:S5778")
  void testSpecificSecondsThrows03() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SecondExpression.SPECIFIC_SECONDS.generateExpression(Arrays.asList(4, 9, 3, null)));
  }

  // Empty list
  @Test
  void testSpecificSecondsThrows04() {
    assertThrows(
        IllegalArgumentException.class, SecondExpression.SPECIFIC_SECONDS::generateExpression);
  }

  @Test
  void testEverySecondIntervalBetween() {
    var expected = "12-24/2";
    var actual = SecondExpression.EVERY_SECOND_INTERVAL_BETWEEN.generateExpression(12, 24, 2);
    assertEquals(expected, actual);
  }

  // Empty list
  @Test
  void testEverySecondIntervalBetweenThrows01() {
    assertThrows(
        IllegalArgumentException.class,
        SecondExpression.EVERY_SECOND_INTERVAL_BETWEEN::generateExpression);
  }

  // First argument is less than 0
  @Test
  void testEverySecondIntervalBetweenThrows02() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SecondExpression.EVERY_SECOND_INTERVAL_BETWEEN.generateExpression(-1, 12, 2));
  }

  // Second argument is less than 0
  @Test
  void testEverySecondIntervalBetweenThrows03() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SecondExpression.EVERY_SECOND_INTERVAL_BETWEEN.generateExpression(1, -12, 2));
  }

  // Third argument is less than 0
  @Test
  void testEverySecondIntervalBetweenThrows04() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SecondExpression.EVERY_SECOND_INTERVAL_BETWEEN.generateExpression(1, 12, -1));
  }

  // First argument is larger than 59
  @Test
  void testEverySecondIntervalBetweenThrows05() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SecondExpression.EVERY_SECOND_INTERVAL_BETWEEN.generateExpression(60, 60, 2));
  }

  // Second argument is larger than 59
  @Test
  void testEverySecondIntervalBetweenThrows06() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SecondExpression.EVERY_SECOND_INTERVAL_BETWEEN.generateExpression(1, 60, 2));
  }

  // Third argument is larger than 59
  @Test
  void testEverySecondIntervalBetweenThrows07() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SecondExpression.EVERY_SECOND_INTERVAL_BETWEEN.generateExpression(1, 12, 60));
  }

  // Second argument is smaller than first argument
  @Test
  void testEverySecondIntervalBetweenThrows08() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SecondExpression.EVERY_SECOND_INTERVAL_BETWEEN.generateExpression(35, 24, 1));
  }

  // Too few arguments
  @Test
  void testEverySecondIntervalBetweenThrows09() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SecondExpression.EVERY_SECOND_INTERVAL_BETWEEN.generateExpression(1, 4));
  }

  // Too many arguments
  @Test
  void testEverySecondIntervalBetweenThrows10() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SecondExpression.EVERY_SECOND_INTERVAL_BETWEEN.generateExpression(1, 4, 8, 12));
  }
}
