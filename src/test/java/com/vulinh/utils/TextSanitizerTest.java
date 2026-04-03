package com.vulinh.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.vulinh.exception.XSSViolationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

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

  @ParameterizedTest
  @ValueSource(
      strings = {
        // Plain text and basic Markdown
        "Hello World",
        "## Heading\n\nSome **bold** and _italic_ text",
        "A [link](https://example.com) in markdown",
        // Java generics in backtick code spans — must pass through as original Markdown
        "`List<String>`",
        "`Map<String, Integer>`",
        "Use `new ArrayList<>()` to create a list",
        // Java generics in fenced code blocks
        """
        ```java
        List<String> items = new ArrayList<>();
        ```
        """,
        """
        ```
        Map<K, V> map;
        ```
        """,
      })
  void testDetectXss_validContent(String input) {
    assertDoesNotThrow(() -> TextSanitizer.detectXss(input, "testField1"));
  }

  @ParameterizedTest
  @NullAndEmptySource
  void testDetectXss_blankInput(String input) {
    assertDoesNotThrow(() -> TextSanitizer.detectXss(input, "testField2"));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "Hello <script>alert(xss)</script> World",
        "<img src=x onerror=alert(1)>Safe",
        "<div onclick=\"alert(1)\">Click me</div>"
      })
  void testDetectXss_maliciousContent(String input) {
    var exception =
        assertThrows(
            XSSViolationException.class,
            () -> TextSanitizer.detectXss(input, "postContent"));

    assertEquals("postContent", exception.getFieldName());
  }
}
