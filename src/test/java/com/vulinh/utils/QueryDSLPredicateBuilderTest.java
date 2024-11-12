package com.vulinh.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.vulinh.data.entity.QUsers;
import org.junit.jupiter.api.Test;

class QueryDSLPredicateBuilderTest {

  @Test
  void testGetFieldName() {
    var expected = "username";

    assertEquals(expected, QueryDSLPredicateBuilder.getFieldName(QUsers.users.username));
  }
}
