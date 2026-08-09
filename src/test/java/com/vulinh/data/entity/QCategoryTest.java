package com.vulinh.data.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class QCategoryTest {

  @Test
  void test() {
    var actual = QCategory.category.displayName.getMetadata().getName();

    assertEquals("displayName", actual);
  }
}
