package com.vulinh.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents a wrapper around an object that defines equivalence based on an extracted ID rather
 * than the object's own {@code equals} method.
 *
 * <p>This class is useful when you want to compare objects based on a specific characteristic or
 * derived identifier, especially for use in collections where standard equality is not desired. The
 * ID and its hash code are computed once at creation time and cached for performance, which is
 * particularly beneficial if the ID extraction or hash code computation is expensive.
 *
 * <h2>Caution</h2>
 *
 * <p>The object used as the {@code id} (extracted by the {@code idExtractor} and stored internally)
 * plays a critical role in both {@code equals} and {@code hashCode} calculations. This {@code id}
 * object should be <strong>immutable</strong> or at least <strong>effectively immutable</strong>
 * after the {@code Equivalence} instance is created, especially if the instance is to be used in
 * hash-based collections (e.g., {@link java.util.HashSet}, {@link java.util.HashMap}). If the state
 * of a mutable {@code id} object changes after the {@code Equivalence} instance's creation:
 *
 * <ul>
 *   <li>The pre-computed {@code hash} (cached at creation) will no longer accurately reflect the
 *       current state of the {@code id}, potentially leading to the {@code Equivalence} instance
 *       being "lost" or misplaced in hash-based collections.
 *   <li>The behavior of {@code equals}, which relies on the {@code id}'s equality, may become
 *       inconsistent with the cached {@code hash}, violating the general contract that equal
 *       objects must have equal hash codes.
 * </ul>
 *
 * <p>This can result in subtle and hard-to-diagnose bugs.
 *
 * <h2>How equality is defined</h2>
 *
 * <p>Equality between two {@code Equivalence} instances is determined by:
 *
 * <ol>
 *   <li>Whether they are both instances of {@code Equivalence}.
 *   <li>The runtime types of the wrapped {@code value} objects are compatible. Specifically, the
 *       {@code value} from one instance must be an instance of the {@code Class} of the {@code
 *       value} from the other instance, or vice versa. This allows for comparison across related
 *       types in a hierarchy (e.g., {@code Equivalence<Dog>} and {@code Equivalence<Animal>}).
 *   <li>Whether the extracted {@code id} objects are equal, according to the specified {@link
 *       EqualityDeepness} of this instance (e.g., shallow or deep comparison, especially for
 *       arrays).
 * </ol>
 *
 * <p>The hash code is derived from the extracted {@code id}, calculated according to the specified
 * {@link EqualityDeepness}.
 *
 * @param <T> The type of the wrapped value.
 * @param value The original value object. This object is stored, but its {@code equals} and {@code
 *     hashCode} methods are not directly used for the equivalence comparison of this wrapper.
 * @param id The extracted identifier from the {@code value}, used for {@code equals} and {@code
 *     hashCode}. Its comparison and hashing are governed by the chosen {@code equalityDeepness}.
 *     <strong>Crucially, this object should be immutable or effectively immutable</strong> once the
 *     {@code Equivalence} instance is created to ensure consistent behavior, especially in
 *     collections (see class-level caution).
 * @param equalityDeepness The strategy for comparing the extracted {@code id} values and
 *     calculating its hash.
 * @param hash The cached hash code of the {@code id}, calculated according to the specified {@link
 *     EqualityDeepness}.
 */
public record Equivalence<T>(T value, Object id, EqualityDeepness equalityDeepness, int hash) {

  /**
   * Defines the strategy for comparing extracted ID objects and calculating their hash codes. This
   * allows for choosing between shallow or deep equality semantics, particularly for arrays.
   */
  @RequiredArgsConstructor
  public enum EqualityDeepness {

    /**
     * Uses {@link Objects#equals(Object, Object)} for ID comparison and {@link
     * Objects#hashCode(Object)} for hash code calculation. This performs a shallow comparison for
     * arrays (i.e., compares array references, not contents).
     */
    SHALLOW_EQUAL(Objects::equals, Objects::hashCode),

    /**
     * Uses {@link Objects#deepEquals(Object, Object)} for ID comparison and a custom deep hash code
     * calculation ({@link #handleGetHashCode(Object)}) for arrays. This is suitable for IDs that
     * are arrays where content equality is desired.
     */
    DEEP_EQUAL(Objects::deepEquals, EqualityDeepness::handleGetHashCode);

    /**
     * Calculates a hash code for an object, with special handling for arrays. For arrays, it uses
     * the appropriate {@code Arrays.hashCode} for primitive arrays or {@link
     * Arrays#deepHashCode(Object[])} for object arrays to ensure content-based hashing. For
     * non-array objects, it delegates to {@link Objects#hashCode(Object)}.
     *
     * @param object The object to hash.
     * @return The hash code.
     */
    private static int handleGetHashCode(Object object) {
      if (object.getClass().isArray()) {
        return switch (object) {
          case int[] intArray -> Arrays.hashCode(intArray);
          case long[] longArray -> Arrays.hashCode(longArray);
          case byte[] byteArray -> Arrays.hashCode(byteArray);
          case short[] shortArray -> Arrays.hashCode(shortArray);
          case boolean[] booleanArray -> Arrays.hashCode(booleanArray);
          case char[] charArray -> Arrays.hashCode(charArray);
          case double[] doubleArray -> Arrays.hashCode(doubleArray);
          case float[] floatArray -> Arrays.hashCode(floatArray);
          default -> Arrays.deepHashCode((Object[]) object);
        };
      }

      return Objects.hashCode(object);
    }

    /** The predicate used to compare two ID objects for equality. */
    private final BiPredicate<Object, Object> idComparator;

    /** The function used to calculate the hash code of an ID object. */
    private final ToIntFunction<Object> hashCalculator;

    /**
     * Compares two ID objects for equality using the configured strategy.
     *
     * @param id1 The first ID object.
     * @param id2 The second ID object.
     * @return {@code true} if the IDs are considered equal, {@code false} otherwise.
     */
    private boolean compareId(Object id1, Object id2) {
      return idComparator.test(id1, id2);
    }

    /**
     * Calculates the hash code of an ID object using the configured strategy.
     *
     * @param object The ID object.
     * @return The hash code.
     */
    private int calculateHashCode(Object object) {
      return hashCalculator.applyAsInt(object);
    }
  }

  /**
   * A factory class for creating {@link Equivalence} instances. This provides convenient static
   * methods to construct {@code Equivalence} objects with default or specified {@link
   * EqualityDeepness}.
   */
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Creator {

    /**
     * Creates a new {@link Equivalence} instance using {@link EqualityDeepness#SHALLOW_EQUAL} for
     * ID comparison.
     *
     * <p>The ID is extracted from the provided {@code value} using the {@code idExtractor}
     * function. The {@code value} and {@code idExtractor} must not be null, and the {@code
     * idExtractor} must not return a null ID. The hash code of the extracted ID is pre-computed and
     * stored.
     *
     * @param <T> The type of the value.
     * @param value The value to wrap. Must not be null.
     * @param idExtractor A function to extract the ID from the value. The function must not return
     *     null. The object returned by this extractor (the ID) should ideally be <strong>immutable
     *     or effectively immutable</strong> to prevent issues with pre-computed hash codes and
     *     equality checks (see class-level caution on {@link Equivalence}).
     * @return A new {@link Equivalence} instance.
     * @throws NullPointerException if {@code value} is null, or if {@code idExtractor} is null, or
     *     if {@code idExtractor} returns null.
     */
    public static <T> Equivalence<T> of(T value, Function<? super T, ?> idExtractor) {
      return of(value, idExtractor, EqualityDeepness.SHALLOW_EQUAL);
    }

    /**
     * Creates a new {@link Equivalence} instance with a specified {@link EqualityDeepness} for ID
     * comparison.
     *
     * <p>The ID is extracted from the provided {@code value} using the {@code idExtractor}
     * function. All parameters ({@code value}, {@code idExtractor}, {@code equalityDeepness}) must
     * not be null, and the {@code idExtractor} must not return null. The hash code of the extracted
     * ID is pre-computed based on the specified {@code equalityDeepness} and stored.
     *
     * @param <T> The type of the value.
     * @param value The value to wrap. Must not be null.
     * @param idExtractor A function to extract the ID from the value. The function must not return
     *     null. The object returned by this extractor (the ID) should ideally be <strong>immutable
     *     or effectively immutable</strong> to prevent issues with pre-computed hash codes and
     *     equality checks (see class-level caution on {@link Equivalence}).
     * @param equalityDeepness The strategy for comparing IDs and calculating their hash codes. Must
     *     not be null.
     * @return A new {@link Equivalence} instance.
     * @throws NullPointerException if {@code value}, {@code idExtractor}, or {@code
     *     equalityDeepness} is null, or if {@code idExtractor} returns null.
     */
    public static <T> Equivalence<T> of(
        T value, Function<? super T, ?> idExtractor, EqualityDeepness equalityDeepness) {
      Objects.requireNonNull(value, "Value cannot be null");
      Objects.requireNonNull(idExtractor, "ID extractor function cannot be null");
      Objects.requireNonNull(equalityDeepness, "Equality deepness cannot be null");

      var id = Objects.requireNonNull(idExtractor.apply(value), "ID cannot be null");

      return new Equivalence<>(value, id, equalityDeepness, equalityDeepness.calculateHashCode(id));
    }

    /**
     * Creates a stream of distinct original objects (of type {@code T}) from a given collection.
     * Distinctness is determined by comparing an ID extracted from each object, using a specified
     * {@link EqualityDeepness}.
     *
     * <p>The process involves:
     *
     * <ol>
     *   <li>Filtering the input collection to retain only elements from which a valid ID can be
     *       extracted (see {@link #isValidId(Object, Function)}).
     *   <li>Wrapping these valid elements into {@link Equivalence} instances, using the provided
     *       {@code idExtractor} and {@code equalityDeepness}. The {@code Equivalence} wrapper
     *       defines equality based on the extracted ID.
     *   <li>Applying {@link Stream#distinct()} to the stream of {@code Equivalence} objects. This
     *       operation uses the {@code Equivalence} class's {@code equals} and {@code hashCode}
     *       methods, thereby ensuring uniqueness based on the extracted IDs.
     *   <li>Unwrapping each distinct {@link Equivalence} object back to its original value (of type
     *       {@code T}).
     * </ol>
     *
     * The resulting stream contains the original objects, but with duplicates (as defined by
     * ID-based equivalence) removed.
     *
     * @param <T> The upper bound of the types of elements in the collection, and the type of
     *     elements in the resulting stream.
     * @param collection The collection of objects to process. Can contain elements of type {@code
     *     T} or any subtype of {@code T}. Must not be null.
     * @param idExtractor A function to extract the ID from each element. Must not be null, and must
     *     not return null for elements that pass the initial validity filter. The object returned
     *     by this extractor (the ID) should ideally be <strong>immutable or effectively
     *     immutable</strong> (see class-level caution on {@link Equivalence}).
     * @param equalityDeepness The strategy for comparing IDs and calculating their hash codes. Must
     *     not be null.
     * @return A stream of distinct original objects (of type {@code T}), where distinctness is
     *     determined by their extracted IDs and the specified {@code equalityDeepness}.
     * @throws NullPointerException if {@code collection}, {@code idExtractor}, or {@code
     *     equalityDeepness} is null.
     */
    public static <T> Stream<T> buildDistinctEquivalenceStream(
        Collection<? extends T> collection,
        Function<? super T, ?> idExtractor,
        EqualityDeepness equalityDeepness) {
      Objects.requireNonNull(collection, "Collection cannot be null");
      Objects.requireNonNull(idExtractor, "ID extractor function cannot be null");
      Objects.requireNonNull(equalityDeepness, "Equality deepness cannot be null");

      return collection.stream()
          .filter(e -> isValidId(e, idExtractor))
          .map(e -> of(e, idExtractor, equalityDeepness))
          .distinct()
          .map(Equivalence::unwrap);
    }

    /**
     * Checks if a valid ID can be extracted from the given element. An element is considered to
     * have a valid ID if:
     *
     * <ol>
     *   <li>The element itself ({@code e}) is not null.
     *   <li>The ID extracted by {@code idExtractor} is not null.
     *   <li>If the extracted ID is a {@link CharSequence}, it is not blank (checked using {@link
     *       StringUtils#isNotBlank(CharSequence)}).
     * </ol>
     *
     * @param <T> The type of the element.
     * @param e The element to check.
     * @param idExtractor The function to extract the ID from the element.
     * @return {@code true} if a valid ID can be extracted, {@code false} otherwise.
     * @throws NullPointerException if {@code idExtractor} is null and {@code e} is not null (as
     *     {@code idExtractor.apply(e)} would be called). However, {@code
     *     buildDistinctEquivalenceStream} ensures {@code idExtractor} is not null.
     */
    static <T> boolean isValidId(T e, Function<? super T, ?> idExtractor) {
      if (e == null) {
        return false;
      }

      Object id = idExtractor.apply(e);

      return id instanceof CharSequence s ? StringUtils.isNotBlank(s) : id != null;
    }
  }

  /**
   * Indicates whether some other object is "equal to" this {@code Equivalence} instance.
   *
   * <p>Two {@code Equivalence} instances are considered equal if all the following conditions are
   * met:
   *
   * <ol>
   *   <li>The {@code other} object is also an instance of {@code Equivalence}.
   *   <li>The wrapped {@code value} objects are type-compatible across a class hierarchy.
   *       Specifically, the {@code value} from one instance (e.g., {@code this.value()}) must be an
   *       instance of the {@code Class} object representing the type of the {@code value} from the
   *       other instance (e.g., {@code that.value().getClass()}), or vice versa. This allows for
   *       comparisons such as between an {@code Equivalence<Dog>} and an {@code
   *       Equivalence<Animal>} (assuming their IDs match), as a {@code Dog} object is an instance
   *       of the {@code Animal} class.
   *   <li>The extracted {@code id} of this instance is equal to the extracted {@code id} of the
   *       {@code other} instance, as determined by the configured {@link EqualityDeepness} of this
   *       instance (which is used by {@code equalityDeepness().compareId(id(), that.id())}).
   * </ol>
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

    // Check for type compatibility: one value must be an instance of the other's class.
    // This allows Equivalence<Dog> to be compared with Equivalence<Animal> if Dog extends Animal.
    return (thatValue.getClass().isInstance(value) || value.getClass().isInstance(thatValue))
        && equalityDeepness().compareId(id(), that.id());
  }

  /**
   * Returns the hash code value for this {@code Equivalence} instance.
   *
   * <p>The hash code is pre-computed at construction time and is based on the hash code of the
   * extracted {@code id} object, calculated according to the configured {@link EqualityDeepness}.
   *
   * @return The hash code value for this object, derived from the {@code id} and {@code
   *     equalityDeepness}.
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
