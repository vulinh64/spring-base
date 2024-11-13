package com.vulinh.data.base;

@FunctionalInterface
public interface RecordIdentifiable<I> extends Identifiable<I> {

  @Override
  default I getId() {
    return id();
  }

  I id();
}
