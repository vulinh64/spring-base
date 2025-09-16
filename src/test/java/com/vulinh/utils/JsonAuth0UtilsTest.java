package com.vulinh.utils;

import module java.base;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class JsonAuth0UtilsTest {

  @Test
  void toMinimizedJSON() {
    var queue = new LinkedList<>();

    queue.add(1);
    queue.add(2);
    queue.add(3);

    var expected = "[1,2,3]";
    var actual = JsonUtils.toMinimizedJSON(queue);

    assertEquals(expected, actual);
  }
}
