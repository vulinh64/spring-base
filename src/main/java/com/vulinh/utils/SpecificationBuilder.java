package com.vulinh.utils;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.Collection;
import java.util.function.BinaryOperator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * @deprecated Gradually replaced by {@link PredicateBuilder}
 */
@Deprecated(forRemoval = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpecificationBuilder {

  // 1 = 1
  @NonNull
  public static <E> Specification<E> always() {
    return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
  }

  // 1 != 1
  @NonNull
  public static <E> Specification<E> never() {
    return (root, query, criteriaBuilder) -> criteriaBuilder.disjunction();
  }

  // field = value
  @Nullable
  public static <E, F> Specification<E> eq(
      SingularAttribute<? super E, F> attribute, @Nullable F value) {
    return value == null
        ? null
        : (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(attribute), value);
  }

  // field != value
  @Nullable
  public static <E, F> Specification<E> neq(
      SingularAttribute<? super E, F> attribute, @Nullable F value) {
    return value == null
        ? null
        : (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(attribute), value);
  }

  // field > value
  @Nullable
  public static <E, F extends Comparable<? super F>> Specification<E> ge(
      SingularAttribute<? super E, F> attribute, @Nullable F value) {
    return value == null
        ? null
        : (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(attribute), value);
  }

  // field >= value
  @Nullable
  public static <E, F extends Comparable<? super F>> Specification<E> geq(
      SingularAttribute<? super E, F> attribute, @Nullable F value) {
    return value == null
        ? null
        : (root, query, criteriaBuilder) ->
            criteriaBuilder.greaterThanOrEqualTo(root.get(attribute), value);
  }

  // field < value
  @Nullable
  public static <E, F extends Comparable<? super F>> Specification<E> le(
      SingularAttribute<? super E, F> attribute, @Nullable F value) {
    return value == null
        ? null
        : (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get(attribute), value);
  }

  // field <= value
  @Nullable
  public static <E, F extends Comparable<? super F>> Specification<E> leq(
      SingularAttribute<? super E, F> attribute, @Nullable F value) {
    return value == null
        ? null
        : (root, query, criteriaBuilder) ->
            criteriaBuilder.lessThanOrEqualTo(root.get(attribute), value);
  }

  // field >= lower_value and field <= upper_value
  @NonNull
  public static <E, F extends Comparable<? super F>> Specification<E> between(
      SingularAttribute<? super E, F> attribute, @NonNull F lowerBound, @NonNull F upperBound) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.between(root.get(attribute), lowerBound, upperBound);
  }

  // field > lower_value and field < upper_value
  @NonNull
  public static <E, F extends Comparable<? super F>> Specification<E> exclusiveBetween(
      SingularAttribute<? super E, F> attribute, @NonNull F lowerBound, @NonNull F upperBound) {
    return and(ge(attribute, lowerBound), le(attribute, upperBound));
  }

  // field >= lower_value and field < upper_value
  @NonNull
  public static <E, F extends Comparable<? super F>> Specification<E> lowerExclusiveBetween(
      SingularAttribute<? super E, F> attribute, @NonNull F lowerBound, @NonNull F upperBound) {
    return and(ge(attribute, lowerBound), leq(attribute, upperBound));
  }

  // field > lower_value and field <= upper_value
  @NonNull
  public static <E, F extends Comparable<? super F>> Specification<E> upperExclusiveBetween(
      SingularAttribute<? super E, F> attribute, @NonNull F lowerBound, @NonNull F upperBound) {
    return and(geq(attribute, lowerBound), le(attribute, upperBound));
  }

  // field in (collection)
  @Nullable
  public static <E, F> Specification<E> in(
      SingularAttribute<? super E, F> attribute, Collection<? extends F> collection) {
    return CollectionUtils.isEmpty(collection)
        ? null
        : (root, query, criteriaBuilder) -> root.get(attribute).in(collection);
  }

  // field not in (collection)
  @Nullable
  public static <E, F> Specification<E> notIn(
      SingularAttribute<? super E, F> attribute, Collection<? extends F> collection) {
    return CollectionUtils.isEmpty(collection)
        ? null
        : Specification.not(in(attribute, collection));
  }

  // field is null
  public static <E> Specification<E> isNull(SingularAttribute<? super E, ?> attribute) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get(attribute));
  }

  // field is not null
  public static <E> Specification<E> notNull(SingularAttribute<? super E, ?> attribute) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get(attribute));
  }

  // field = true
  public static <E> Specification<E> isTrue(SingularAttribute<? super E, Boolean> attribute) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get(attribute));
  }

  // field = false
  public static <E> Specification<E> isFalse(SingularAttribute<? super E, Boolean> attribute) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get(attribute));
  }

  // field like '%pattern%' escape '\\'
  // if pattern is empty, it is equal to always()
  @Nullable
  @SuppressWarnings("unchecked")
  public static <E, F> Specification<E> like(
      SingularAttribute<? super E, F> attribute, @Nullable String keyword) {
    // Wrap the pattern keyword with % character as prefix and suffix
    var patternKeyword =
        StringUtils.isBlank(keyword)
            ? StringUtils.EMPTY
            : "%%%s%%".formatted(EscapeCharacter.DEFAULT.escape(keyword));

    if (StringUtils.isBlank(patternKeyword)) {
      return null;
    }

    return (root, query, criteriaBuilder) ->
        criteriaBuilder.like(
            criteriaBuilder.lower(
                String.class.isAssignableFrom(attribute.getJavaType())
                    ? root.get((SingularAttribute<E, String>) attribute)
                    : root.get(attribute).as(String.class)),
            criteriaBuilder.lower(criteriaBuilder.literal(patternKeyword)));
  }

  // field not like '%pattern%' escape '\\'
  // if pattern is empty, it is equal to never()
  @Nullable
  public static <E, F> Specification<E> notLike(
      SingularAttribute<? super E, F> attribute, String keyword) {
    var likeSpecification = SpecificationBuilder.<E, F>like(attribute, keyword);

    return likeSpecification == null ? null : Specification.not(likeSpecification);
  }

  @SafeVarargs
  @NonNull
  public static <E> Specification<E> and(Specification<E>... specifications) {
    return combineSpecifications(SpecificationOperation.AND, Specification::and, specifications);
  }

  @SafeVarargs
  @NonNull
  public static <E> Specification<E> or(Specification<E>... specifications) {
    return combineSpecifications(SpecificationOperation.OR, Specification::or, specifications);
  }

  @SafeVarargs
  @NonNull
  private static <E> Specification<E> combineSpecifications(
      SpecificationOperation specificationOperation,
      BinaryOperator<Specification<E>> combiner,
      Specification<E>... specifications) {
    if (ArrayUtils.isEmpty(specifications)) {
      return SpecificationBuilder.always();
    }

    var result = specificationOperation.<E>getFirstChainSpecification();

    for (var specification : specifications) {
      if (specification != null) {
        result = combiner.apply(result, specification);
      }
    }

    return result;
  }

  @RequiredArgsConstructor
  public enum SpecificationOperation {
    AND(SpecificationBuilder.always()),
    OR(SpecificationBuilder.never());

    private final Specification<?> firstChainSpecification;

    @SuppressWarnings("unchecked")
    private <T> Specification<T> getFirstChainSpecification() {
      return (Specification<T>) firstChainSpecification;
    }
  }
}
