package com.vulinh.utils;

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
        "Hello World",
        "Hello <b>World</b>",
        "<i>Italic</i> and <em>emphasis</em>",
        "<a href=\"https://example.com\">Link</a>",
        "## Markdown heading\n\nSome **bold** text"
      })
  void testValidateAndPassThrough_validContent(String input) {
    assertEquals(input, TextSanitizer.validateAndPassThrough(input, "testField"));
  }

  @ParameterizedTest
  @NullAndEmptySource
  void testValidateAndPassThrough_blankInput(String input) {
    assertEquals(input, TextSanitizer.validateAndPassThrough(input, "testField"));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "Hello <script>alert(xss)</script> World",
        "<img src=x onerror=alert(1)>Safe",
        "<div onclick=\"alert(1)\">Click me</div>"
      })
  void testValidateAndPassThrough_maliciousContent(String input) {
    var exception =
        assertThrows(
            XSSViolationException.class,
            () -> TextSanitizer.validateAndPassThrough(input, "postContent"));

    assertEquals("postContent", exception.getFieldName());
  }
}
