package com.vulinh.utils;

import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.exception.CommonException;
import com.vulinh.service.post.create.PostCreationValidationService;
import java.security.SecureRandom;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostUtils {

  public static String createPostSlug(@NonNull String title) {
    var randomNumber = generateRandomNumber();

    // Remove character accents, normalize white spaces and turn text to lowercase
    var result =
        "%s-%d"
            .formatted(
                StringUtils.stripAccents(StringUtils.normalizeSpace(title))
                    .toLowerCase()
                    // Replace white space to "-"
                    .replace(' ', '-')
                    // Replace Vietnamese character "đ" by "d"
                    .replace("đ", "d"),
                randomNumber);

    if (result.length() > PostCreationValidationService.SLUG_MAX_LENGTH) {
      throw new CommonException(
          "Post's slug exceeded %d characters"
              .formatted(PostCreationValidationService.SLUG_MAX_LENGTH),
          CommonMessage.MESSAGE_POST_INVALID_SLUG);
    }

    return result;
  }

  private static int generateRandomNumber() {
    try {
      var result = SecureRandom.getInstanceStrong().nextInt();

      // Use positive value only
      return result == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(result);
    } catch (Exception exception) {
      throw new CommonException(
          "Unable to generate random number", CommonMessage.MESSAGE_INTERNAL_ERROR, exception);
    }
  }
}
