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

  // New UUID length is only 32 (after removing 4 dashes)
  private static final int UUID_LENGTH = 32;
  private static final int SLUG_MAX_LENGTH = 5000 + UUID_LENGTH + 1;

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

  private static String generateRandomUUID() {
    return NoDashedUUIDGenerator.createNonDashedUUID(UUID.randomUUID());
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  static class NoDashedUUIDGenerator {

    private static final char[] HEXADECIMAL_DIGITS = {
      '0', '1', '2', '3', '4', '5',
      '6', '7', '8', '9', 'a', 'b',
      'c', 'd', 'e', 'f'
    };

    /*
     * UUID format:
     * XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX
     * ^        ^    ^    ^    ^
     * 1st      2nd  3rd  4th  5th
     * (8)      (4)  (4)  (4)  (12)
     */

    // The length of the first part of the UUID (12)
    static final int FIFTH_PART_SIZE = 12;

    // The length of the fourth to second parts of the UUID (4)
    static final int SECOND_TO_FOURTH_SIZE = 4;

    // The length of the first part of the UUID
    static final int FIRST_PART_SIZE = 8;

    // Should be 20
    static final int OFFSET_AFTER_FIFTH_PART = UUID_LENGTH - FIFTH_PART_SIZE;

    // Should be 16
    static final int OFFSET_AFTER_FOURTH_PART = OFFSET_AFTER_FIFTH_PART - SECOND_TO_FOURTH_SIZE;

    // Should be 12
    static final int OFFSET_AFTER_THIRD_PART = OFFSET_AFTER_FOURTH_PART - SECOND_TO_FOURTH_SIZE;

    // Should be 8
    static final int OFFSET_AFTER_SECOND_PART = OFFSET_AFTER_THIRD_PART - SECOND_TO_FOURTH_SIZE;

    // Should be 0
    static final int REMAINED_OFFSET = OFFSET_AFTER_SECOND_PART - FIRST_PART_SIZE;

    // Taken directly from Long.class
    public static String createNonDashedUUID(UUID uuid) {
      var leastSignificantBits = uuid.getLeastSignificantBits();
      var mostSignificantBits = uuid.getMostSignificantBits();

      var buffer = new byte[UUID_LENGTH];

      formatUnsignedLong(buffer, leastSignificantBits, FIFTH_PART_SIZE, OFFSET_AFTER_FIFTH_PART);

      formatUnsignedLong(
          buffer, leastSignificantBits >>> 48, SECOND_TO_FOURTH_SIZE, OFFSET_AFTER_FOURTH_PART);

      formatUnsignedLong(
          buffer, mostSignificantBits, SECOND_TO_FOURTH_SIZE, OFFSET_AFTER_THIRD_PART);

      formatUnsignedLong(
          buffer, mostSignificantBits >>> 16, SECOND_TO_FOURTH_SIZE, OFFSET_AFTER_SECOND_PART);

      formatUnsignedLong(
          buffer, mostSignificantBits >>> UUID_LENGTH, FIRST_PART_SIZE, REMAINED_OFFSET);

      return new String(buffer);
    }

    private static void formatUnsignedLong(byte[] buffer, long value, int length, int offset) {
      var characterPosition = offset + length;
      var radix = 1 << 4;
      var mask = radix - 1;

      do {
        buffer[--characterPosition] = (byte) HEXADECIMAL_DIGITS[((int) value) & mask];
        value >>>= 4;
      } while (characterPosition > offset);
    }
  }
}
