package com.vulinh.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
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
