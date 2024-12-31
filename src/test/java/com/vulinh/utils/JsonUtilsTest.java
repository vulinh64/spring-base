package com.vulinh.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import org.junit.jupiter.api.Test;

class JsonUtilsTest {

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

  // I am not supposed to test this method
  @Test
  void toPrettyJSON() {
    // Fuck line separator
    System.setProperty("line.separator", "\n");

    var queue = new LinkedList<>();

    queue.add(1);
    queue.add(2);
    queue.add(3);

    var expected =
        """
        [
          1,
          2,
          3
        ]""";
    var actual = JsonUtils.toPrettyJSON(queue);

    assertEquals(expected, actual);
  }
}
