package com.vulinh.utils;

import com.vulinh.exception.XSSViolationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextSanitizer {

  static final Safelist DEFAULT_SAFELIST = Safelist.relaxed().addTags("details", "summary");

  public static String sanitize(String text) {
    return StringUtils.isBlank(text) ? text : Jsoup.clean(text, DEFAULT_SAFELIST);
  }

  public static String validateAndPassThrough(String text, String fieldName) {
    if (StringUtils.isBlank(text) || Jsoup.isValid(text, DEFAULT_SAFELIST)) {
      return text;
    }

    throw XSSViolationException.of(fieldName, text, Jsoup.clean(text, DEFAULT_SAFELIST));
  }
}
