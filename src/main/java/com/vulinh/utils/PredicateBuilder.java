package com.vulinh.utils;

import module java.base;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.lang.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PredicateBuilder {

  private static final char ESCAPE = '\\';

  public static Predicate always() {
    return Expressions.asBoolean(true).isTrue();
  }

  public static Predicate never() {
    return always().not();
  }

  public static <T> Predicate likeIgnoreCase(Expression<T> expression, String keyword) {
    // Spaces are considered none
    if (StringUtils.isBlank(keyword)) {
      return always();
    }

    var patternKeyword = "%%%s%%".formatted(EscapeCharacter.DEFAULT.escape(keyword));

    if (expression instanceof StringExpression se) {
      return se.likeIgnoreCase(patternKeyword, ESCAPE);
    }

    var stringOperation = Expressions.stringOperation(Ops.STRING_CAST, expression);

    return stringOperation.likeIgnoreCase(patternKeyword, ESCAPE);
  }

  public static Predicate and(Predicate firstPredicate, Predicate... predicates) {
    return combinePredicates(BooleanBuilder::and, firstPredicate, predicates);
  }

  public static Predicate or(Predicate firstPredicate, Predicate... predicates) {
    return combinePredicates(BooleanBuilder::or, firstPredicate, predicates);
  }

  @NonNull
  public static String getFieldName(@NonNull Path<?> expression) {
    return expression.getMetadata().getName();
  }

  private static Predicate combinePredicates(
      BiFunction<BooleanBuilder, Predicate, Predicate> combiner,
      Predicate firstPredicate,
      Predicate... otherPredicates) {
    var builder = new BooleanBuilder(firstPredicate);

    if (ArrayUtils.isNotEmpty(otherPredicates)) {
      for (var predicate : otherPredicates) {
        combiner.apply(builder, predicate);
      }
    }

    return builder;
  }
}
