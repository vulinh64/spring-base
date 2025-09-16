package com.vulinh.utils;

import module java.base;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

/// A record that holds an object of generic type T along with an order value. This class provides functionality to
/// track the original position of objects in a collection while allowing for custom sorting operations.
///
/// This class ensures that the wrapped value is never null and the order is never negative.
///
/// @param <T> the type of object being wrapped
public record OrderedObject<T>(T value, int order) {

  /// Utility class providing static methods for creating and manipulating OrderedObject instances.
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Wrapper {

    /// Creates a new OrderedObject that wraps the given object with the specified order.
    ///
    /// @param <T> the type of the object to wrap
    /// @param object the object to wrap, must not be null
    /// @param order the order value to assign
    /// @return a new OrderedObject containing the given object and order
    public static <T> OrderedObject<T> wrap(@NonNull T object, int order) {
      return new OrderedObject<>(object, order);
    }

    /// Creates a Comparator that first compares OrderedObject instances by their natural order using the provided key
    /// extractor function, then by their order value. This method uses ascending order and places null values last.
    ///
    /// @param <T> the type of objects wrapped in OrderedObject
    /// @param <U> the type of the key used for comparison, must be Comparable
    /// @param keyExtractor a function to extract the comparison key from the wrapped object
    /// @return a Comparator for OrderedObject instances
    public static <T, U extends Comparable<? super U>>
        Comparator<OrderedObject<T>> firstCompareByNaturalOrder(Function<T, U> keyExtractor) {
      return firstCompareBy(keyExtractor, Order.ASCENDING, NullsOrder.NULLS_LAST);
    }

    /// Creates a Comparator that first compares OrderedObject instances using the provided key extractor function,
    /// then by their order value. The comparison order and null handling strategy can be specified.
    ///
    /// @param <T> the type of objects wrapped in OrderedObject
    /// @param <U> the type of the key used for comparison, must be Comparable
    /// @param keyExtractor a function to extract the comparison key from the wrapped object, must not be null
    /// @param order the sorting order (ascending or descending), must not be null
    /// @param nullsOrder the strategy for handling null values, must not be null
    /// @return a Comparator for OrderedObject instances
    /// @throws NullPointerException if any parameter is null
    public static <T, U extends Comparable<? super U>> Comparator<OrderedObject<T>> firstCompareBy(
        Function<T, U> keyExtractor, Order order, NullsOrder nullsOrder) {
      Objects.requireNonNull(keyExtractor, "Object extractor function cannot be null");
      Objects.requireNonNull(order, "Order direction cannot be null");
      Objects.requireNonNull(nullsOrder, "Nulls order strategy cannot be null");

      return Comparator.<OrderedObject<T>, U>comparing(
              wrapper -> keyExtractor.apply(wrapper.value()),
              nullsOrder.<U>toNullsOrder(order.toDirection()))
          .thenComparing(order.toOrderComparator());
    }

    /// Creates a sorted list from the original list based on the provided key extractor and sorting preferences. The
    /// original order of elements is preserved for elements that compare as equal.
    ///
    /// @param <T> the type of elements in the list
    /// @param <U> the type of the key used for comparison, must be Comparable
    /// @param originalList the list to sort, must not contain null elements
    /// @param keyExtractor a function to extract the comparison key from each element
    /// @param order the sorting order (ascending or descending)
    /// @param nullsOrder the strategy for handling null keys
    /// @return a new sorted list containing all elements from the original list
    /// @throws NullPointerException if originalList, keyExtractor, order, or nullsOrder is null
    /// @throws IllegalArgumentException if originalList contains any null elements
    public static <T, U extends Comparable<? super U>> List<T> toSortedList(
        @NonNull List<T> originalList,
        Function<T, U> keyExtractor,
        Order order,
        NullsOrder nullsOrder) {
      Objects.requireNonNull(keyExtractor, "Key extractor cannot be null");
      Objects.requireNonNull(order, "Order cannot be null");
      Objects.requireNonNull(nullsOrder, "Nulls order cannot be null");

      if (originalList.isEmpty()) {
        return Collections.emptyList();
      }

      return IntStream.range(0, originalList.size())
          .mapToObj(index -> wrapNonNull(originalList, index))
          .sorted(firstCompareBy(keyExtractor, order, nullsOrder))
          .map(OrderedObject::unwrap)
          .toList();
    }

    private static <T> OrderedObject<T> wrapNonNull(List<T> originalList, int index) {
      var value = originalList.get(index);

      if (value == null) {
        throw new IllegalArgumentException("List cannot contain any null object");
      }

      return wrap(value, index);
    }
  }

  /// Enum representing strategies for handling null values during comparison operations.
  public enum NullsOrder {
    /// Place null values before non-null values in sorting results.
    NULLS_FIRST,

    /// Place null values after non-null values in sorting results.
    NULLS_LAST,

    /// Reject null values with an exception during comparison.
    NULLS_HOSTILE;

    /// Transforms a comparator according to the null-handling strategy.
    ///
    /// @param <U> the type of objects being compared
    /// @param comparator the base comparator to transform
    /// @return a comparator with the appropriate null-handling behavior
    <U> Comparator<U> toNullsOrder(Comparator<U> comparator) {
      return switch (this) {
        case NULLS_FIRST -> Comparator.nullsFirst(comparator);
        case NULLS_LAST -> Comparator.nullsLast(comparator);
        default ->
            (left, right) -> {
              if (left == null || right == null) {
                throw new IllegalArgumentException("Null values not allowed!");
              }

              return comparator.compare(left, right);
            };
      };
    }
  }

  /// Enum representing the direction of sorting operations.
  public enum Order {
    /// Sort elements in ascending order.
    ASCENDING,

    /// Sort elements in descending order.
    DESCENDING;

    /// Returns a comparator that sorts in the direction specified by this enum value.
    ///
    /// @param <U> the type of objects being compared, must be Comparable
    /// @return a comparator for the specified direction
    <U extends Comparable<? super U>> Comparator<U> toDirection() {
      return this == ASCENDING ? Comparator.naturalOrder() : Comparator.reverseOrder();
    }

    /// Returns a comparator that sorts OrderedObject instances by their order field in the direction
    /// specified by this enum value.
    ///
    /// @param <U> the type of objects wrapped in OrderedObject
    /// @return a comparator for OrderedObject instances based on their order field
    <U> Comparator<OrderedObject<U>> toOrderComparator() {
      return this == ASCENDING
          ? Comparator.comparingInt(OrderedObject::order)
          : Comparator.<OrderedObject<U>>comparingInt(OrderedObject::order).reversed();
    }
  }

  /// Creates a new OrderedObject with the specified value and order.
  ///
  /// @param value the value to store, must not be null
  /// @param order the order value, must not be negative
  /// @throws IllegalArgumentException if value is null or order is negative
  public OrderedObject {
    if (value == null) {
      throw new IllegalArgumentException("Object cannot be null");
    }

    if (order < 0) {
      throw new IllegalArgumentException("Order cannot be negative");
    }
  }

  /// Unwraps and returns the value stored in this OrderedObject.
  ///
  /// @return the wrapped value, never null
  @NonNull
  public T unwrap() {
    return value;
  }
}
