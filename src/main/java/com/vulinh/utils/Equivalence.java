package com.vulinh.utils;

import java.util.Objects;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Represents a wrapper around an object that defines equivalence based on an extracted ID rather
 * than the object's own {@code equals} method.
 *
 * <p>This class is useful when you want to compare objects based on a specific characteristic or
 * derived identifier, especially for use in collections where standard equality is not desired. The
 * ID and its hash code are computed once at creation time and cached for performance, which is
 * particularly beneficial if the ID extraction or hash code computation is expensive.
 *
 * <p>Equality between two {@code Equivalence} instances is determined by:
 *
 * <ul>
 *   <li>Whether they are both instances of {@code Equivalence}.
 *   <li>Whether the class of the wrapped {@code value} in one instance is assignable from the class
 *       of the wrapped {@code value} in the other, or vice versa. This allows for comparison across
 *       related types in a hierarchy.
 *   <li>Whether the extracted {@code id} objects are equal according to their own {@code equals}
 *       method.
 * </ul>
 *
 * <p>The hash code is derived directly from the hash code of the extracted {@code id}.
 *
 * @param <T> The type of the wrapped value.
 * @param value The original value object. This object is stored but its {@code equals} and {@code
 *     hashCode} methods are not directly used for the equivalence comparison of this wrapper.
 * @param id The extracted identifier from the {@code value}, used for {@code equals} and {@code
 *     hashCode}. This ID should have a reliable {@code equals} and {@code hashCode} implementation.
 * @param hash The cached hash code of the {@code id}.
 */
public record Equivalence<T>(T value, Object id, int hash) {

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Creator {

    /**
     * Creates a new {@link Equivalence} instance.
     *
     * <p>The ID is extracted from the provided value using the {@code idExtractor} function. Both
     * the value and the extracted ID must not be null. The hash code of the extracted ID is
     * pre-computed and stored.
     *
     * @param <T> The type of the value.
     * @param value The value to wrap. Must not be null.
     * @param idExtractor A function to extract the ID from the value. The function must not return
     *     null.
     * @return A new {@link Equivalence} instance.
     * @throws NullPointerException if {@code value} is null, or if {@code idExtractor} is null, or
     *     if {@code idExtractor} returns null.
     */
    public static <T> Equivalence<T> of(T value, Function<? super T, ?> idExtractor) {
      Objects.requireNonNull(value, "Value cannot be null");
      Objects.requireNonNull(idExtractor, "ID extractor function cannot be null");

      var id = Objects.requireNonNull(idExtractor.apply(value), "ID cannot be null");

      return new Equivalence<>(value, id, id.hashCode());
    }
  }

  /**
   * Indicates whether some other object is "equal to" this {@code Equivalence} instance.
   *
   * <p>Two {@code Equivalence} instances are considered equal if all the following conditions are
   * met:
   *
   * <ul>
   *   <li>The {@code other} object is also an instance of {@code Equivalence}.
   *   <li>The wrapped {@code value} objects are type-compatible across a class hierarchy.
   *       Specifically, the {@code value} from one instance (e.g., {@code this.value()}) must be an
   *       instance of the {@code Class} object representing the type of the {@code value} from the
   *       other instance (e.g., {@code that.value().getClass()}), or vice versa. This allows for
   *       comparisons such as between an {@code Equivalence<Dog>} and an {@code
   *       Equivalence<Animal>} (assuming their IDs match), as a {@code Dog} object is an instance
   *       of the {@code Animal} class.
   *   <li>The extracted {@code id} of this instance is equal to the extracted {@code id} of the
   *       {@code other} instance, as determined by {@link Objects#equals(Object, Object)}.
   * </ul>
   *
   * @param other The reference object with which to compare.
   * @return {@code true} if this {@code Equivalence} instance is considered equal to the {@code
   *     other} argument based on the criteria above; {@code false} otherwise.
   */
  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Equivalence<?> that)) {
      return false;
    }

    var thatValue = that.value();

    return (thatValue.getClass().isInstance(value) || value.getClass().isInstance(thatValue))
        && Objects.equals(id, that.id());
  }

  /**
   * Returns the hash code value for this {@code Equivalence} instance.
   *
   * <p>The hash code is pre-computed at construction time and is based solely on the hash code of
   * the extracted {@code id} object.
   *
   * @return The hash code value for this object, derived from the {@code id}.
   */
  @Override
  public int hashCode() {
    return hash;
  }

  /**
   * Retrieves the original value object wrapped by this {@code Equivalence} instance.
   *
   * <p>This method provides the same functionality as the automatically generated {@link #value()}
   * accessor method for the {@code value} record component. It is offered as an alternative for
   * enhanced readability in contexts where "unwrap" more clearly conveys the action of retrieving
   * the underlying object.
   *
   * @return The original value object.
   */
  public T unwrap() {
    return value;
  }
}
