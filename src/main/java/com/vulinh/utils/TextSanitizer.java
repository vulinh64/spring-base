package com.vulinh.utils;

import com.vulinh.exception.XSSViolationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextSanitizer {

  static final String YOUTUBE_TAG = "youtube";

  static final String[] SAFE_TAGS = {"details", "summary", "hr", YOUTUBE_TAG};

  static final Safelist DEFAULT_SAFELIST =
      Safelist.relaxed()
          .addTags(SAFE_TAGS)
          .addAttributes(YOUTUBE_TAG, "url")
          // CommonMark renders fenced code blocks as <code class="language-xxx">
          .addAttributes("code", "class");

  static final Parser MARKDOWN_PARSER = Parser.builder().build();

  static final HtmlRenderer MARKDOWN_RENDERER = HtmlRenderer.builder().build();

  /*
   * Validates Markdown content by converting it to HTML first, then checking the resulting HTML
   * against the safelist. Returns the original Markdown unchanged if valid, or throws if the
   * rendered HTML contains disallowed elements (XSS).
   */
  public static void detectXss(String text, String fieldName) {
    if (StringUtils.isBlank(text)) {
      return;
    }

    var html = MARKDOWN_RENDERER.render(MARKDOWN_PARSER.parse(text));

    if (!Jsoup.isValid(html, DEFAULT_SAFELIST)) {
      throw XSSViolationException.of(fieldName, text, sanitize(html));
    }
  }

  /*
   * Sanitizes plain-text-like fields (title, excerpt, slug). Not for Markdown content.
   */
  public static String sanitize(String text) {
    return StringUtils.isBlank(text) ? text : Jsoup.clean(text, DEFAULT_SAFELIST);
  }
}
