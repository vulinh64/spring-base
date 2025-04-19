package com.vulinh.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.vulinh.utils.WithOrderWrapper.NullsOrder;
import com.vulinh.utils.WithOrderWrapper.Order;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WithOrderWrapperTest {

  private record TestUser(int id, String name, int age) {}

  static final List<TestUser> TEST_LIST =
      List.of(
          new TestUser(1, null, 23),
          new TestUser(2, "Bob", 30),
          new TestUser(3, "Alice", 29),
          new TestUser(4, "Clive", 27),
          new TestUser(5, "Alice", 25));

  @Test
  @DisplayName(
      "When sorting with ascending order and nulls first, items should be sorted alphabetically with nulls at beginning")
  void testAscendingNullFirstName() {
    // Expected order: 1, 3, 5, 2, 4
    var result = buildResult(Order.ASCENDING, NullsOrder.NULLS_FIRST);

    assertCorrectOrder(result, 1, 3, 5, 2, 4);
  }

  @Test
  @DisplayName(
      "When sorting with ascending order and nulls last, items should be sorted alphabetically with nulls at end")
  void testAscendingNullLastName() {
    // Expected order: 3, 5, 2, 4, 1
    var result = buildResult(Order.ASCENDING, NullsOrder.NULLS_LAST);

    assertCorrectOrder(result, 3, 5, 2, 4, 1);
  }

  @Test
  @DisplayName(
      "When sorting with descending order and nulls first, items should be sorted reverse alphabetically with nulls at beginning")
  void testDescendingNullFirstName() {
    // Expected order: 1, 4, 2, 5, 3
    var result = buildResult(Order.DESCENDING, NullsOrder.NULLS_FIRST);

    assertCorrectOrder(result, 1, 4, 2, 5, 3);
  }

  @Test
  @DisplayName(
      "When sorting with descending order and nulls last, items should be sorted reverse alphabetically with nulls at end")
  void testDescendingNullLastName() {
    // Expected order: 4, 2, 5, 3, 1
    var result = buildResult(Order.DESCENDING, NullsOrder.NULLS_LAST);

    assertCorrectOrder(result, 4, 2, 5, 3, 1);
  }

  @Test
  @DisplayName("Default firstCompareByNaturalOrder should sort ascending with nulls last")
  void testNormalAsAscendingNullLast() {
    var result =
        IntStream.range(0, TEST_LIST.size())
            .mapToObj(i -> WithOrderWrapper.wrap(TEST_LIST.get(i), i))
            .sorted(WithOrderWrapper.firstCompareByNaturalOrder(TestUser::name))
            .map(WithOrderWrapper::unwrap)
            .toList();

    assertCorrectOrder(result, 3, 5, 2, 4, 1);
  }

  @Test
  @DisplayName("Constructor should throw exception when object is null")
  void assertThrowObjectNull() {
    assertThrows(Exception.class, () -> new WithOrderWrapper<>(null, 0));
  }

  @Test
  @DisplayName("Constructor should throw exception when order is negative")
  void assertThrowOrderNegative() {
    assertThrows(Exception.class, () -> new WithOrderWrapper<>(new Object(), -1));
  }

  @Test
  @DisplayName(
      """
      Throw IllegalArgumentException if the object contains null field, perhaps indicating a failed validation\
      """)
  void throwWhenNullElementDetected() {
    assertThrows(
        IllegalArgumentException.class, WithOrderWrapperTest::nullDetectedLaunchNuclearMissile);
  }

  @Test
  @DisplayName("Luckily sort a list that contains no element with one of its field null")
  void noThrowWhenUsingNullHostile() {
    assertDoesNotThrow(
        () -> {
          var testList = new ArrayList<>(TEST_LIST);

          testList.removeFirst();

          IntStream.range(0, testList.size())
              .mapToObj(indices -> WithOrderWrapper.wrap(testList.get(indices), indices))
              .sorted(
                  WithOrderWrapper.firstCompareBy(
                      TestUser::name, Order.ASCENDING, NullsOrder.NULLS_HOSTILE))
              .map(WithOrderWrapper::unwrap)
              .toList();
        });

    assertDoesNotThrow(
        () -> {
          WithOrderWrapper.toSortedList(
              Collections.emptyList(), TestUser::name, Order.DESCENDING, NullsOrder.NULLS_HOSTILE);
        });
  }

  private static List<TestUser> buildResult(Order order, NullsOrder nullsOrder) {
    return IntStream.range(0, TEST_LIST.size())
        .mapToObj(indices -> WithOrderWrapper.wrap(TEST_LIST.get(indices), indices))
        .sorted(WithOrderWrapper.firstCompareBy(TestUser::name, order, nullsOrder))
        .map(WithOrderWrapper::unwrap)
        .toList();
  }

  private static void assertCorrectOrder(List<TestUser> result, int... expectedOrders) {
    IntStream.range(0, expectedOrders.length)
        .forEach(i -> assertEquals(expectedOrders[i], result.get(i).id()));
  }

  private static void nullDetectedLaunchNuclearMissile() {
    var testList = new ArrayList<TestUser>();

    testList.add(new TestUser(6, null, 25));
    testList.add(new TestUser(7, null, 26));
    testList.addFirst(new TestUser(8, null, 28));
    testList.addFirst(new TestUser(8, "X", 29));

    assertNotNull(
        WithOrderWrapper.toSortedList(
            testList, TestUser::name, Order.ASCENDING, NullsOrder.NULLS_HOSTILE));
  }
}
