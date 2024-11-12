package com.vulinh.data.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QCategoryTest {

  @Test
  void test() {
    var actual = QCategory.category.displayName.getMetadata().getName();

    assertEquals("displayName", actual);
  }
}
