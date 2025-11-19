package com.vulinh.utils.post;

import module java.base;

import com.vulinh.exception.ValidationException;
import com.vulinh.locale.ServiceErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SlugUtils {

  static final int SLUG_MAX_LENGTH = 5000 + NoDashedUUIDGenerator.UUID_LENGTH + 1;

  public static String createPostSlug(@NonNull String title) {
    var randomUUID = generateRandomUUID();

    // Remove character accents, normalize white spaces and turn text to lowercase
    var result = "%s-%s".formatted(createBasicSlug(title), randomUUID);

    if (result.length() > SLUG_MAX_LENGTH) {
      throw ValidationException.validationException(
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
        .replace("đ", "d");
  }

  public static String generateRandomUUID() {
    return NoDashedUUIDGenerator.createNonDashedUUID(UUID.randomUUID());
  }
}
