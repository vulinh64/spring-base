package com.vulinh.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import org.springframework.lang.NonNull;

/**
 * A wrapper that associates an object of type {@code T} with an integer order index.
 *
 * <p>This class is intended for sorting collections based on both a key extracted from the wrapped
 * objects and their original positions (order). It provides utilities to wrap, sort, and unwrap
 * objects while preserving secondary ordering for equal keys.
 *
 * @param <T> the type of the wrapped object
 */
public record WithOrderWrapper<T>(T object, int order) {

  /** Strategies for handling {@code null} values during comparison. */
  public enum NullsOrder {
    /** Treat {@code null} values as less than non-null values (appear first). */
    NULLS_FIRST,
    /** Treat {@code null} values as greater than non-null values (appear last). */
    NULLS_LAST,
    /** Do not allow {@code null} values; throw an exception if encountered. */
    NULLS_HOSTILE;

    /**
     * Wraps a base {@link Comparator} with null-handling according to this strategy.
     *
     * @param <U> the type compared by the base comparator
     * @param comparator the comparator for non-null values
     * @return a comparator that applies null-handling first, then delegates to {@code comparator}
     */
    <U> Comparator<U> toNullsOrder(Comparator<U> comparator) {
      return switch (this) {
        case NULLS_FIRST -> Comparator.nullsFirst(comparator);
        case NULLS_LAST -> Comparator.nullsLast(comparator);
        default ->
            (o1, o2) -> {
              if (o1 == null || o2 == null) {
                throw new IllegalArgumentException("Null values not allowed!");
              }

              return comparator.compare(o1, o2);
            };
      };
    }
  }

  /** Ordering directions for comparisons: ascending or descending. */
  public enum Order {
    /** Natural ascending order. */
    ASCENDING,
    /** Reverse (descending) order. */
    DESCENDING;

    /**
     * Returns a {@link Comparator} for {@link Comparable} types based on this ordering.
     *
     * @param <U> the type of elements to compare
     * @return {@code Comparator.naturalOrder()} if ascending, or {@code Comparator.reverseOrder()}
     *     if descending
     */
    <U extends Comparable<? super U>> Comparator<U> toOrder() {
      return this == ASCENDING ? Comparator.naturalOrder() : Comparator.reverseOrder();
    }

    /**
     * Returns a comparator based on applying a function to extract an integer key, then ordering by
     * that key in this direction.
     *
     * @param <U> the type of elements to compare
     * @param function a function extracting an int from each element
     * @return a comparator comparing the extracted ints in ascending or descending order
     */
    <U> Comparator<U> toIndexOrder(ToIntFunction<U> function) {
      return this == ASCENDING
          ? Comparator.comparingInt(function)
          : Comparator.comparingInt(function).reversed();
    }
  }

  /**
   * Canonical constructor that enforces non-null object and non-negative order.
   *
   * @param object the object to wrap, must not be null
   * @param order the order index, must be non-negative
   * @throws IllegalArgumentException if {@code object} is null or {@code order} is negative
   */
  public WithOrderWrapper {
    if (object == null) {
      throw new IllegalArgumentException("Object cannot be null");
    }

    if (order < 0) {
      throw new IllegalArgumentException("Order cannot be negative");
    }
  }

  /**
   * Utility method to wrap an object with the given order index.
   *
   * @param <T> the type of the object
   * @param object the object to wrap, must not be null
   * @param order the order index, must be non-negative
   * @return a new {@code WithOrderWrapper} instance containing {@code object}
   */
  public static <T> WithOrderWrapper<T> wrap(@NonNull T object, int order) {
    return new WithOrderWrapper<>(object, order);
  }

  /**
   * Creates a comparator for {@code WithOrderWrapper} that first extracts a key via {@code
   * keyExtractor}, compares by natural ascending order with nulls last, then by the original index
   * to break ties.
   *
   * @param <T> the type of wrapped objects
   * @param <U> the comparable key type
   * @param keyExtractor function to extract a key from the wrapped object
   * @return a comparator that orders wrappers by key then original index
   */
  public static <T, U extends Comparable<? super U>>
      Comparator<WithOrderWrapper<T>> firstCompareByNaturalOrder(Function<T, U> keyExtractor) {
    return firstCompareBy(keyExtractor, Order.ASCENDING, NullsOrder.NULLS_LAST);
  }

  /**
   * Creates a comparator for {@code WithOrderWrapper} that first extracts a key via {@code
   * keyExtractor}, compares by specified order and nulls handling, then by the original index to
   * break ties.
   *
   * @param <T> the type of wrapped objects
   * @param <U> the comparable key type
   * @param keyExtractor function to extract a key from the wrapped object
   * @param order sorting direction for the key (ascending/descending)
   * @param nullsOrder strategy for handling null keys
   * @return a comparator that orders wrappers by key then original index
   * @throws NullPointerException if {@code keyExtractor} or {@code order} is null
   */
  public static <T, U extends Comparable<? super U>> Comparator<WithOrderWrapper<T>> firstCompareBy(
      Function<T, U> keyExtractor, Order order, NullsOrder nullsOrder) {
    Objects.requireNonNull(keyExtractor, "Object extractor function cannot be null");
    Objects.requireNonNull(order, "Order direction cannot be null");
    Objects.requireNonNull(nullsOrder, "Nulls order strategy cannot be null");

    return Comparator.<WithOrderWrapper<T>, U>comparing(
            wrapper -> keyExtractor.apply(wrapper.object()),
            nullsOrder.<U>toNullsOrder(order.toOrder()))
        .thenComparing(order.toIndexOrder(WithOrderWrapper::order));
  }

  /**
   * Sorts the given list by a key extracted from its elements, preserving the original order for
   * equal keys.
   *
   * @param <T> the element type of the list
   * @param <U> the comparable key type
   * @param originalList the list to sort (maybe empty)
   * @param keyExtractor function to extract the sort key from each element
   * @param order sorting direction (ascending/descending)
   * @param nullsOrder strategy for handling null keys
   * @return a new sorted list; empty if {@code originalList} is empty
   * @throws NullPointerException if {@code keyExtractor} or {@code order} is null
   */
  public static <T, U extends Comparable<? super U>> List<T> toSortedList(
      List<T> originalList, Function<T, U> keyExtractor, Order order, NullsOrder nullsOrder) {
    if (originalList.isEmpty()) {
      return Collections.emptyList();
    }

    return IntStream.range(0, originalList.size())
        .mapToObj(index -> WithOrderWrapper.wrap(originalList.get(index), index))
        .sorted(WithOrderWrapper.firstCompareBy(keyExtractor, order, nullsOrder))
        .map(WithOrderWrapper::unwrap)
        .toList();
  }

  /**
   * Unwraps and returns the original object.
   *
   * @return the wrapped object, never null
   */
  @NonNull
  public T unwrap() {
    return object;
  }
}
