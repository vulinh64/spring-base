package com.vulinh.utils;

import module java.base;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/// Represents a wrapper around an object that defines equivalence based on an extracted ID rathe than the object's 
/// own `equals()` method.
/// 
/// This class is useful when you want to compare objects based on a specific characteristic or derived identifier, 
/// especially for use in collections where standard equality is not desired. The ID and its hash code are computed 
/// once at creation time and cached for performance, which is particularly beneficial if the ID extraction or hash 
/// code computation is expensive.
/// 
/// ## Caution
/// 
/// The object used as the `id` (extracted by the `idExtractor` and stored internally) plays a critical role in both 
/// `equals()` and `hashCode()` calculations. This `id` object should be **immutable** or at least 
/// **effectively immutable** after the [Equivalence] instance is created, especially if the instance is to be used in
/// hash-based collections (e.g., [java.util.HashSet], [java.util.HashMap]). If the state of a mutable `id` object 
/// changes after the [Equivalence] instance's creation:
/// 
/// - The pre-computed `hash` (cached at creation) will no longer accurately reflect the current state of the `id`,
/// potentially leading to the [Equivalence] instance being "lost" or misplaced in hash-based collections.
///
/// - The behavior of `equals`, which relies on the `id`'s equality, may become inconsistent with the cached `hash`,
/// violating the general contract that equal objects must have equal hash codes.
///     
/// This can result in subtle and hard-to-diagnose bugs.
///
/// ## How equality is defined
/// 
/// Equality between two [Equivalence] instances is determined by:
///
/// 1. Whether they are both instances of [Equivalence].
///
/// 2. The runtime types of the wrapped `value` objects are compatible. Specifically, the `value` from one instance
/// must be an instance of the `Class` of the `value` from the other instance, or vice versa. This allows for
/// comparison across related types in a hierarchy (e.g., [Equivalence]`<Dog>` and [Equivalence]`<Animal>`).
///
/// 3. Whether the extracted `id` objects are equal, according to the specified [EqualityDeepness] of this instance
/// (e.g., shallow or deep comparison, especially for arrays).
///
/// The hash code is derived from the extracted `id`, calculated according to the specified [EqualityDeepness].
///
/// @param <T> The type of the wrapped value.
/// @param value The original value object. This object is stored, but its `equals` and  `hashCode` methods are not
/// directly used for the equivalence comparison of this wrapper.
/// @param id The extracted identifier from the `value`, used for `equals` and `hashCode`. Its comparison and hashing
/// are governed by the chosen `equalityDeepness`. **Crucially, this object should be immutable or effectively
/// immutable** once the [Equivalence] instance is created to ensure consistent behavior, especially in collections
/// (see class-level caution).
/// @param equalityDeepness The strategy for comparing the extracted `id` values and calculating its hash.
/// @param hash The cached hash code of the `id`, calculated according to the specified [EqualityDeepness].
public record Equivalence<T>(T value, Object id, EqualityDeepness equalityDeepness, int hash) {

  /// Defines the strategy for comparing extracted ID objects and calculating their hash codes. This allows for
  /// choosing between shallow or deep equality semantics, particularly for arrays.
  @RequiredArgsConstructor
  public enum EqualityDeepness {

    /// Uses `equals(Object,Object)` for ID comparison and `hashCode(Object)` for hash code calculation. This performs
    /// a shallow comparison for arrays (i.e., compares array references, not contents).
    SHALLOW_EQUAL(Objects::equals, Objects::hashCode),

    /// Uses `deepEquals(Object,Object)` for ID comparison and a custom deep hash code calculation
    /// ([#handleGetHashCode(Object)]) for arrays. This is suitable for IDs that are arrays where content equality is
    /// desired.
    DEEP_EQUAL(Objects::deepEquals, EqualityDeepness::handleGetHashCode);

    /// Calculates a hash code for an object, with special handling for arrays. For arrays, it uses the appropriate
    /// `Arrays.hashCode` for primitive arrays or `deepHashCode(Object[])` for object arrays to ensure content-based
    /// hashing. Fornon-array objects, it delegates to `hashCode(Object)`.
    ///
    /// @param object The object to hash.
    /// @return The hash code.
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

    /// The predicate used to compare two ID objects for equality.
    private final BiPredicate<Object, Object> idComparator;

    /// The function used to calculate the hash code of an ID object.
    private final ToIntFunction<Object> hashCalculator;

    /// Compares two ID objects for equality using the configured strategy.
    ///
    /// @param id1 The first ID object.
    /// @param id2 The second ID object.
    /// @return `true` if the IDs are considered equal, `false` otherwise.
    private boolean compareId(Object id1, Object id2) {
      return idComparator.test(id1, id2);
    }

    /// Calculates the hash code of an ID object using the configured strategy.
    ///
    /// @param object The ID object.
    /// @return The hash code.
    private int calculateHashCode(Object object) {
      return hashCalculator.applyAsInt(object);
    }
  }

  /// A factory class for creating [Equivalence] instances. This provides convenient static methods to construct
  /// [Equivalence] objects with default or specified [EqualityDeepness].
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Creator {

    /// Creates a new [Equivalence] instance using [#SHALLOW_EQUAL] for ID comparison.
    ///
    /// The ID is extracted from the provided `value` using the `idExtractor` function. The `value` and `idExtractor`
    /// must not be null, and the `idExtractor` must not return a null ID. The hash code of the extracted ID is
    /// pre-computed and stored.
    ///
    /// @param <T> The type of the value.
    /// @param value The value to wrap. Must not be null.
    /// @param idExtractor A function to extract the ID from the value. The function must not return null. The object
    /// returned by this extractor (the ID) should ideally be **immutable or effectively immutable** to prevent issues
    /// with pre-computed hash codes and equality checks (see class-level caution on [Equivalence]).
    /// @return A new [Equivalence] instance.
    /// @throws NullPointerException if `value` is null, or if `idExtractor` is null, or
    ///     if `idExtractor` returns null.
    public static <T> Equivalence<T> of(T value, Function<? super T, ?> idExtractor) {
      return of(value, idExtractor, EqualityDeepness.SHALLOW_EQUAL);
    }

    /// Creates a new [Equivalence] instance with a specified [EqualityDeepness] for ID comparison.
    ///
    /// The ID is extracted from the provided `value` using the `idExtractor` function. All parameters (`value`,
    /// `idExtractor`, `equalityDeepness`) must not be null, and the `idExtractor` must not return null. The hash code
    /// of the extracted ID is pre-computed based on the specified `equalityDeepness` and stored.
    ///
    /// @param <T> The type of the value.
    /// @param value The value to wrap. Must not be null.
    /// @param idExtractor A function to extract the ID from the value. The function must not return null. The object
    /// returned by this extractor (the ID) should ideally be **immutable or effectively immutable** to prevent issues
    /// with pre-computed hash codes and equality checks (see class-level caution on [Equivalence]).
    /// @param equalityDeepness The strategy for comparing IDs and calculating their hash codes. Must not be null.
    /// @return A new [Equivalence] instance.
    /// @throws NullPointerException if `value`, `idExtractor`, or `equalityDeepness` is null, or if `idExtractor`
    /// returns null.
    public static <T> Equivalence<T> of(
        T value, Function<? super T, ?> idExtractor, EqualityDeepness equalityDeepness) {
      Objects.requireNonNull(value, "Value cannot be null");
      Objects.requireNonNull(idExtractor, "ID extractor function cannot be null");
      Objects.requireNonNull(equalityDeepness, "Equality deepness cannot be null");

      var id = Objects.requireNonNull(idExtractor.apply(value), "ID cannot be null");

      return new Equivalence<>(value, id, equalityDeepness, equalityDeepness.calculateHashCode(id));
    }

    /// Creates a stream of distinct original objects (of type `T`) from a given collection.
    ///
    /// Distinctness is determined by comparing an ID extracted from each object, using a specified [EqualityDeepness].
    ///
    /// The process involves:
    ///
    /// - Filtering the input collection to retain only elements from which a valid ID can be extracted (see
    /// [#isValidId(Object,Function)]).
    ///
    /// - Wrapping these valid elements into [Equivalence] instances, using the provided `idExtractor` and
    /// `equalityDeepness`. The `Equivalence` wrapper defines equality based on the extracted ID.
    ///
    /// - Applying [#distinct()] to the stream of `Equivalence` objects. This operation uses the `Equivalence` class's
    /// `equals` and `hashCode` methods, thereby ensuring uniqueness based on the extracted IDs.
    ///
    /// - Unwrapping each distinct [Equivalence] object back to its original value (of type `T`).
    ///
    /// The resulting stream contains the original objects, but with duplicates (as defined by ID-based equivalence)
    /// removed.
    ///
    /// @param <T> The upper bound of the types of elements in the collection, and the type of elements in the
    /// resulting stream.
    /// @param collection The collection of objects to process. Can contain elements of type `T` or any subtype of `T`.
    /// Must not be null.
    /// @param idExtractor A function to extract the ID from each element. Must not be null, and must not return null
    /// for elements that pass the initial validity filter. The object returned by this extractor (the ID) should
    /// ideally be **immutable or effectively immutable** (see class-level caution on [Equivalence]).
    /// @param equalityDeepness The strategy for comparing IDs and calculating their hash codes. Must not be null.
    /// @return A stream of distinct original objects (of type `T`), where distinctness is determined by their
    /// extracted IDs and the specified `equalityDeepness`.
    /// @throws NullPointerException if `collection`, `idExtractor`, or `equalityDeepness` is null.
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

    /// Checks if a valid ID can be extracted from the given element. An element is considered to have a valid ID if:
    ///
    /// - The element itself (`e`) is not null.
    ///
    /// - The ID extracted by `idExtractor` is not null.
    ///
    /// - If the extracted ID is a [CharSequence], it is not blank (checked using `isNotBlank(CharSequence)`).
    ///
    /// @param <T> The type of the element.
    /// @param e The element to check.
    /// @param idExtractor The function to extract the ID from the element.
    /// @return `true` if a valid ID can be extracted, `false` otherwise.
    /// @throws NullPointerException if `idExtractor` is null and `e` is not null (as `idExtractor.apply(e)` would be
    /// called). However, `buildDistinctEquivalenceStream` ensures `idExtractor` is not null.
    static <T> boolean isValidId(T e, Function<? super T, ?> idExtractor) {
      if (e == null) {
        return false;
      }

      Object id = idExtractor.apply(e);

      return id instanceof CharSequence s ? StringUtils.isNotBlank(s) : id != null;
    }
  }

  /// Indicates whether some other object is "equal to" this `Equivalence` instance.
  ///
  /// Two `Equivalence` instances are considered equal if all the following conditions are met:
  ///
  /// - The `other` object is also an instance of `Equivalence`.
  ///
  /// - The wrapped `value` objects are type-compatible across a class hierarchy. Specifically, the `value` from one
  /// instance (e.g., `this.value()`) must be an instance of the `Class` object representing the type of the `value`
  /// from the other instance (e.g., `that.value().getClass()`), or vice versa. This allows for comparisons such as
  /// between an `Equivalence<Dog>` and an `Equivalence<Animal>` (assuming their IDs match), as a `Dog` object is an
  /// instance of the `Animal` class.
  ///
  /// - The extracted `id` of this instance is equal to the extracted `id` of the `other` instance, as determined by
  /// the configured [EqualityDeepness] of this instance (which is used by `equalityDeepness().compareId(id(),
  /// that.id())`).
  ///
  /// @param other The reference object with which to compare.
  /// @return `true` if this `Equivalence` instance is considered equal to the `other` argument based on the criteria above; `false` otherwise.
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

  /// Returns the hash code value for this `Equivalence` instance.
  ///
  /// The hash code is pre-computed at construction time and is based on the hash code of the extracted `id` object,
  /// calculated according to the configured [EqualityDeepness].
  ///
  /// @return The hash code value for this object, derived from the `id` and `equalityDeepness`.
  @Override
  public int hashCode() {
    return hash;
  }

  /// Retrieves the original value object wrapped by this `Equivalence` instance.
  ///
  /// This method provides the same functionality as the automatically generated [#value()] accessor method for the
  /// `value` record component. It is offered as an alternative for enhanced readability in contexts where "unwrap"
  /// more clearly conveys the action of retrieving the underlying object.
  ///
  /// @return The original value object.
  public T unwrap() {
    return value;
  }
}
