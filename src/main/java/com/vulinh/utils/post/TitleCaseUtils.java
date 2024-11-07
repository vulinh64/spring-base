package com.vulinh.utils.post;

import java.util.Set;
import java.util.StringJoiner;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TitleCaseUtils {

  private static final Set<String> MINOR_WORDS =
      Set.of(
          "a", "an", "the", "and", "but", "or", "nor", "so", "yet", "at", "by", "for", "in", "of",
          "off", "on", "to", "up", "via", "as");

  private static final Set<Character> SPLITTERS = Set.of(';', ':', '.');

  @Nullable
  public static String toTitleCase(@Nullable String input) {
    if (StringUtils.isBlank(input)) {
      return input;
    }

    // Respect already upper case words
    var words = input.split("\\s+");

    var joiner = new StringJoiner(" ");

    for (var index = 0; index < words.length; index++) {
      var currentWord = words[index];

      joiner.add(
          index != 0
                  && index != words.length - 1
                  && MINOR_WORDS.contains(currentWord)
                  && !SPLITTERS.contains(words[index - 1].charAt(words[index - 1].length() - 1))
              ? currentWord
              : StringUtils.capitalize(currentWord));
    }

    return joiner.toString();
  }
}
