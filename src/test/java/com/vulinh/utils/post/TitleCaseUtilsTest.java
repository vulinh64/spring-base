package com.vulinh.utils.post;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TitleCaseUtilsTest {

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      value = {
        "this is a simple sentence|This Is a Simple Sentence",
        "the quick brown fox jumps over the lazy dog|The Quick Brown Fox Jumps Over the Lazy Dog",
        "hello world, how are you doing today?|Hello World, How Are You Doing Today?",
        "hELLO wOrLD, hOw aRe YoU dOiNg tOdAy?|HELLO WOrLD, HOw ARe YoU DOiNg TOdAy?",
        "a journey to the center of the earth| A Journey to the Center of the Earth",
        "an unexpected journey| An Unexpected Journey",
        "war and peace| War and Peace",
        "to be or not to be| To Be or Not to Be",
        "harry potter and the philosophers stone| Harry Potter and the Philosophers Stone",
        "hello, world!| Hello, World!",
        "the adventures of tom sawyer, by mark twain| The Adventures of Tom Sawyer, by Mark Twain",
        "COVID-19: a global pandemic| COVID-19: A Global Pandemic",
        "a man. the wolf. hunt or be hunted|A Man. The Wolf. Hunt or Be Hunted",
        "not only it's a story; it serves as THE reminder|Not Only It's a Story; It Serves as THE Reminder",
        "artificial intelligence, machine learning, and deep learning| Artificial Intelligence, Machine Learning, and Deep Learning"
      })
  void testTitleCase(String sentence, String expected) {
    var actual = TitleCaseUtils.toTitleCase(sentence);
    assertEquals(expected, actual);
  }

  @Test
  void testUnexpected01() {
    var expected = "    ";
    var actual = TitleCaseUtils.toTitleCase("    ");
    assertEquals(expected, actual);
  }

  @Test
  void testUnexpected02() {
    var actual = TitleCaseUtils.toTitleCase(null);

    assertNull(actual);
  }
}
