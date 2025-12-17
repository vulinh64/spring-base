package com.vulinh.data.base;

import module java.base;

@FunctionalInterface
public interface RecordIdentifiable<I extends Serializable> extends Identifiable<I> {

  @Override
  default I getId() {
    return id();
  }

  I id();
}
