package com.vulinh.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextSanitizer {

  private static final Safelist DEFAULT_SAFELIST = Safelist.relaxed();

  public static String sanitize(String text) {
    return StringUtils.isBlank(text) ? text : Jsoup.clean(text, DEFAULT_SAFELIST);
  }
}
