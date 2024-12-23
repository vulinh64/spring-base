package com.vulinh.utils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.*;
import java.util.function.BiFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.query.EscapeCharacter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PredicateBuilder {

  private static final char ESCAPE = '\\';

  public static Predicate always() {
    return Expressions.asBoolean(true).isTrue();
  }

  public static Predicate never() {
    return not(always());
  }

  // equals ignore case
  public static Predicate eqic(StringExpression expression, String value) {
    return expression.lower().equalsIgnoreCase(value);
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
    return concatenatePredicates(BooleanBuilder::and, firstPredicate, predicates);
  }

  public static Predicate or(Predicate firstPredicate, Predicate... predicates) {
    return concatenatePredicates(BooleanBuilder::or, firstPredicate, predicates);
  }

  public static Predicate not(Predicate predicate) {
    return predicate == null ? null : predicate.not();
  }

  public static String getFieldName(Path<?> expression) {
    return expression.getMetadata().getName();
  }

  private static Predicate concatenatePredicates(
      BiFunction<BooleanBuilder, Predicate, Predicate> combiner,
      Predicate firstPredicate,
      Predicate... predicates) {
    var builder = new BooleanBuilder(firstPredicate);

    if (ArrayUtils.isNotEmpty(predicates)) {
      for (var predicate : predicates) {
        combiner.apply(builder, predicate);
      }
    }

    return builder;
  }
}
