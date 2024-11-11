package com.vulinh.utils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.lang.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomQueryDslUtils {

  private static final char ESCAPE = '\\';

  public static Predicate always() {
    return Expressions.asBoolean(true).isTrue();
  }

  public static Predicate never() {
    return not(always());
  }

  public static <T> Predicate eq(SimpleExpression<T> expression, T value) {
    return expression.eq(value);
  }

  public static Predicate eqic(StringExpression expression, String value) {
    return expression.lower().equalsIgnoreCase(value);
  }

  public static <T> Predicate neq(SimpleExpression<T> expression, T value) {
    return expression.ne(value);
  }

  public static <T extends Comparable<? super T>> Predicate gt(
      ComparableExpression<T> expression, @NonNull T value) {
    return expression.gt(value);
  }

  public static <T extends Comparable<? super T>> Predicate lt(
      ComparableExpression<T> expression, @NonNull T value) {
    return expression.lt(value);
  }

  public static <T extends Comparable<? super T>> Predicate goe(
      ComparableExpression<T> expression, @NonNull T value) {
    return expression.goe(value);
  }

  public static <T extends Comparable<? super T>> Predicate loe(
      ComparableExpression<T> expression, @NonNull T value) {
    return expression.loe(value);
  }

  public static <T extends Comparable<? super T>> Predicate between(
      ComparableExpression<T> expression, @NonNull T lower, @NonNull T upper) {
    return expression.between(lower, upper);
  }

  public static <T extends Comparable<? super T>> Predicate exclusiveBetween(
      ComparableExpression<T> expression, @NonNull T lower, @NonNull T upper) {
    return and(gt(expression, lower), lt(expression, upper));
  }

  public static <T extends Comparable<? super T>> Predicate lowerInclusiveBetween(
      ComparableExpression<T> expression, @NonNull T lower, @NonNull T upper) {
    return and(goe(expression, lower), lt(expression, upper));
  }

  public static <T extends Comparable<? super T>> Predicate upperInclusiveBetween(
      ComparableExpression<T> expression, @NonNull T lower, @NonNull T upper) {
    return and(gt(expression, lower), loe(expression, upper));
  }

  public static <T> Predicate isNull(SimpleExpression<T> expression) {
    return expression.isNull();
  }

  public static <T> Predicate isNotNull(SimpleExpression<T> expression) {
    return expression.isNotNull();
  }

  @SafeVarargs
  public static <T> Predicate in(SimpleExpression<T> expression, T... values) {
    return in(expression, Arrays.asList(values));
  }

  public static <T> Predicate in(SimpleExpression<T> expression, Collection<? extends T> values) {
    return expression.in(values);
  }

  public static <T> Predicate like(Expression<T> expression, String keyword) {
    return likeExpressionInternal(expression, keyword, false);
  }

  public static <T> Predicate likeIgnoreCase(Expression<T> expression, String keyword) {
    return likeExpressionInternal(expression, keyword, true);
  }

  public static <T> Predicate notLike(Expression<T> expression, String keyword) {
    return not(likeExpressionInternal(expression, keyword, false));
  }

  public static <T> Predicate notLikeIgnoreCase(Expression<T> expression, String keyword) {
    return not(likeExpressionInternal(expression, keyword, true));
  }

  public static Predicate and(Predicate firstPredicate, Predicate... predicates) {
    return concatenatePredicates(BooleanBuilder::and, firstPredicate, predicates);
  }

  public static Predicate or(Predicate firstPredicate, Predicate... predicates) {
    return concatenatePredicates(BooleanBuilder::or, firstPredicate, predicates);
  }

  public static Predicate not(Predicate predicate) {
    return predicate == null ? null : predicate.not();
  }

  private static Predicate likeExpressionInternal(
      Expression<?> expression, String keyword, boolean isCaseInsensitive) {
    // Spaces are considered none
    if (StringUtils.isBlank(keyword)) {
      return always();
    }

    var patternKeyword = "%%%s%%".formatted(EscapeCharacter.DEFAULT.escape(keyword));

    if (expression instanceof StringExpression se) {
      return isCaseInsensitive
          ? se.likeIgnoreCase(patternKeyword, ESCAPE)
          : se.like(patternKeyword, ESCAPE);
    }

    var stringOperation = Expressions.stringOperation(Ops.STRING_CAST, expression);

    return isCaseInsensitive
        ? stringOperation.likeIgnoreCase(patternKeyword, ESCAPE)
        : stringOperation.like(patternKeyword, ESCAPE);
  }

  private static Predicate concatenatePredicates(
      PredicateConcatenationFunction combiner, Predicate firstPredicate, Predicate... predicates) {
    var builder = new BooleanBuilder(firstPredicate);

    if (ArrayUtils.isNotEmpty(predicates)) {
      for (var predicate : predicates) {
        combiner.apply(builder, predicate);
      }
    }

    return builder;
  }

  private interface PredicateConcatenationFunction
      extends BiFunction<BooleanBuilder, Predicate, Predicate> {}
}
