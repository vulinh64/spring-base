package com.vulinh.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class TextSanitizerTest {

  @ParameterizedTest
  @CsvSource({
    // Malicious HTML
    "'Hello <script>alert(xss)</script> World', 'Hello World'",
    "'<img src=x onerror=alert(1)>Safe', '<img>Safe'",
    // Allowed HTML
    "'Hello <b>World</b>', 'Hello <b>World</b>'",
    "'<i>Italic</i>', '<i>Italic</i>'",
    // Plain text
    "'Hello World', 'Hello World'",
    "'Just text', 'Just text'"
  })
  void testSanitize(String input, String expected) {
    assertEquals(expected, TextSanitizer.sanitize(input));
  }

  @ParameterizedTest
  @NullAndEmptySource
  void testSanitizeWithBlankInput(String input) {
    assertEquals(input, TextSanitizer.sanitize(input));
  }
}
