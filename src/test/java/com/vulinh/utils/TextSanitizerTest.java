package com.vulinh.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextSanitizerTest {

  @Test
  void testSanitizeWithMaliciousHtml() {
    String input = "Hello <script>alert('xss')</script> World";
    String expected = "Hello World";
    assertEquals(expected, TextSanitizer.sanitize(input));
  }

  @Test
  void testSanitizeWithAllowedHtml() {
    String input = "Hello <b>World</b>";
    assertEquals(input, TextSanitizer.sanitize(input));
  }

  @Test
  void testSanitizeWithPlainText() {
    String input = "Hello World";
    assertEquals(input, TextSanitizer.sanitize(input));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "   "})
  void testSanitizeWithBlankInput(String input) {
    assertEquals(input, TextSanitizer.sanitize(input));
  }
}
