package com.vulinh.utils.post;

import module java.base;

import com.vulinh.exception.ApplicationValidationException;
import com.vulinh.locale.ServiceErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SlugUtils {

  static final int SLUG_MAX_LENGTH = 5000;

  public static String createPostSlug(@NonNull String title) {
    var result = createBasicSlug(title);

    if (result.length() > SLUG_MAX_LENGTH) {
      throw new ApplicationValidationException(
          "Post's slug exceeded %d characters".formatted(SLUG_MAX_LENGTH),
          ServiceErrorCode.MESSAGE_POST_INVALID_SLUG);
    }

    return result;
  }

  @NonNull
  public static String createBasicSlug(String text) {
    return StringUtils.stripAccents(PostUtils.normalizeText(text))
        // Replace white space to "-"
        .replace(' ', '-')
        // Replace Vietnamese character "đ" by "d"
        .replace("đ", "d")
        // Replace em dash to "-"
        .replace("—", "-")
        // Strip anything not alphanumeric or "-"
        .replaceAll("[^a-zA-Z0-9\\-]", StringUtils.EMPTY);
  }

  public static String resolveUniqueSlug(String baseSlug, Collection<String> existingSlugs) {
    if (existingSlugs.isEmpty() || !existingSlugs.contains(baseSlug)) {
      return baseSlug;
    }

    var maxSuffix =
        existingSlugs.stream()
            .map(slug -> parseSuffix(slug, baseSlug))
            .max(Integer::compareTo)
            .orElse(1);

    return "%s-%d".formatted(baseSlug, maxSuffix + 1);
  }

  private static int parseSuffix(String slug, String baseSlug) {
    if (slug.equals(baseSlug)) {
      return 1;
    }

    // slug must be in the format "baseSlug-<number>"
    var suffix = slug.substring(baseSlug.length() + 1);

    try {
      return Integer.parseInt(suffix);
    } catch (NumberFormatException _) {
      // Not a numeric suffix (e.g. "my-slug-extra-words"), ignore
      return 0;
    }
  }
}
