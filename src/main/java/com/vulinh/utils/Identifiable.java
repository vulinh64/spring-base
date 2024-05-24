package com.vulinh.utils;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface Identifiable<I> {

  I getId();

  static <I> Map<I, Identifiable<I>> toMap(Collection<? extends Identifiable<I>> source) {
    return source.stream().collect(Collectors.toMap(Identifiable::getId, Function.identity()));
  }
}
