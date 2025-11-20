package com.vulinh.utils.post;

import module java.base;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/*
 * Edgy black magic taken directly from UUID#toString() implementation
 *
 * "AD PROFUNDIS!" - Black Prior, when flipping the opponent into the abyss
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoDashedUUIDGenerator {

  /*
   * Taken from java.util.UUID.class
   */

  static final VarHandle LONG =
      MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);

  static final VarHandle INT =
      MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN);

  /*
   * Little-endian order means that the least significant byte is stored first.
   * And at this time, little-endian is predominantly used widely. I suggest that
   * you read the story on how Linus Torvalds ranted about it in this mail thread:
   *
   * https://lore.kernel.org/lkml/CAHk-%3DwgYcOiFvsJzFb%2BHfB4n6Wj6zM5H5EghUMfpXSCzyQVSfA@mail.gmail.com/t/#mce138059dc56014643bbda330810183031ef5c06
   */

  /*
   * UUID format:
   * XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX
   * ^        ^    ^    ^    ^
   * 1st      2nd  3rd  4th  5th
   * (8)      (4)  (4)  (4)  (12)
   *
   * The no-dash version is a 32-characters hexadecimal string
   */

  // Total length of UUID string without dashes
  static final int UUID_LENGTH = 32;

  // Segment sizes
  static final int PART1_SIZE = 8;
  static final int PART2_SIZE = 4;
  static final int PART3_SIZE = 4;
  static final int PART4_SIZE = 4;
  static final int PART5_SIZE = 12;

  // Offsets for each part in the 32-char array
  static final int PART1_OFFSET = 0;
  static final int PART2_OFFSET = PART1_OFFSET + PART1_SIZE; // 8
  static final int PART3_OFFSET = PART2_OFFSET + PART2_SIZE; // 12
  static final int PART4_OFFSET = PART3_OFFSET + PART3_SIZE; // 16
  static final int PART5_OFFSET = PART4_OFFSET + PART4_SIZE; // 20

  // I have no idea about this 24 number, but it works, so it works
  // And I am not going to pretend I understood everything, so go and
  // read the original code in java.util.UUID if you want a deep dive
  // understanding of this black magic.
  // ¯\_(ツ)_/¯
  static final int FINAL_PART_OFFSET = PART5_OFFSET + PART5_SIZE - Long.BYTES; // 24

  // Magic VarHandler trick to write bytes in little-endian order
  public static String createNonDashedUUID(UUID uuid) {
    var leastSignificantBits = uuid.getLeastSignificantBits();
    var mostSignificantBits = uuid.getMostSignificantBits();

    var buffer = new byte[UUID_LENGTH];

    setLong(buffer, PART1_OFFSET, hex8(mostSignificantBits >>> 32));

    var hex8MostSignificantBits = hex8(mostSignificantBits);

    setInt(buffer, PART2_OFFSET, (int) hex8MostSignificantBits);
    setInt(buffer, PART3_OFFSET, (int) (hex8MostSignificantBits >>> 32));

    var hex8LeastSignificantBits = hex8(leastSignificantBits >>> 32);

    setInt(buffer, PART4_OFFSET, (int) (hex8LeastSignificantBits));
    setInt(buffer, PART5_OFFSET, (int) (hex8LeastSignificantBits >>> 32));
    setLong(buffer, FINAL_PART_OFFSET, hex8(leastSignificantBits));

    // Cannot use JavaLangAccess without modifying the compilation options
    return new String(buffer, StandardCharsets.ISO_8859_1);
  }

  static void setInt(byte[] array, int offset, int value) {
    INT.set(array, offset, value);
  }

  static void setLong(byte[] array, int offset, long value) {
    LONG.set(array, offset, value);
  }

  static long hex8(long n) {
    var number = Long.expand(n, 0x0F0F_0F0F_0F0F_0F0FL);

    var m = (number + 0x0606_0606_0606_0606L) & 0x1010_1010_1010_1010L;

    return Long.reverseBytes(((m << 1) + (m >> 1) - (m >> 4)) + 0x3030_3030_3030_3030L + number);
  }
}
