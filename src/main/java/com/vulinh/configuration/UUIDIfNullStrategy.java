package com.vulinh.configuration;

import java.io.Serial;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * Use this strategy only if you want to automatically generate a UUID as the ID for an entity. This
 * applies when the user leaves the <code>@Id</code> field empty.
 *
 * <p>Snippet to use (copy/paste it to the entity's ID field):
 *
 * <pre>{@code
 * @GenericGenerator(name = UUIDIfNullStrategy.GENERATOR_NAME, type = UUIDIfNullStrategy.class)
 * @GeneratedValue(generator = UUIDIfNullStrategy.GENERATOR_NAME)
 * }</pre>
 */
public class UUIDIfNullStrategy implements IdentifierGenerator {

  // It has to be a string literal
  public static final String GENERATOR_NAME = "UUIDIfNullStrategy";

  @Serial private static final long serialVersionUID = -8834326837580306821L;

  @Override
  public String generate(SharedSessionContractImplementor session, Object entity) {
    return Optional.of(session.getEntityPersister(entity.getClass().getName(), entity))
        .map(entityPersister -> entityPersister.getIdentifier(entity, session))
        .map(String::valueOf)
        .filter(StringUtils::isNotBlank)
        .orElseGet(() -> CustomUUID.createNonDashedUUID(UUID.randomUUID()));
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class CustomUUID {

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

    // New UUID length is only 32 (after removing 4 dashes)
    static final int UUID_LENGTH = 32;

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

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class CustomUUID2 {

    static final char[] HEXADECIMAL_DIGITS = {
      '0', '1', '2', '3', '4', '5',
      '6', '7', '8', '9', 'a', 'b',
      'c', 'd', 'e', 'f'
    };

    public static String createNonDashedUUID(UUID uuid) {
      return hexDigitBuilder(uuid.getMostSignificantBits())
          .append(hexDigitBuilder(uuid.getLeastSignificantBits()))
          .toString();
    }

    private static StringBuilder hexDigitBuilder(long value) {
      var hexDigitBuilder = new StringBuilder(16);

      for (var i = 0; i < 16; i++) {
        hexDigitBuilder.append(HEXADECIMAL_DIGITS[(int) (value & 0xf)]);
        value >>>= 4;
      }

      return hexDigitBuilder;
    }
  }
}
