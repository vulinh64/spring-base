package com.vulinh.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.vulinh.utils.Equivalence.Creator;
import com.vulinh.utils.Equivalence.EqualityDeepness;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EquivalenceArrayTest {

  static final String TEST_VALUE = "testValue";

  // Helper method to create Equivalence for brevity
  private <T> Equivalence<String> createEquivalence(T id, EqualityDeepness deepness) {
    return Creator.of(TEST_VALUE, _ -> id, deepness);
  }

  @Nested
  @DisplayName("Tests for int[] IDs")
  class IntArrayIdTests {
    @Test
    void testIntArrayDeepEquality() {
      var eq1 = createEquivalence(new int[] {1, 2, 3}, EqualityDeepness.DEEP_EQUAL);
      var eq2 = createEquivalence(new int[] {1, 2, 3}, EqualityDeepness.DEEP_EQUAL);
      var eq3 = createEquivalence(new int[] {4, 5, 6}, EqualityDeepness.DEEP_EQUAL);
      var eq4 = createEquivalence(new int[] {1, 2}, EqualityDeepness.DEEP_EQUAL);

      assertEquals(eq1, eq2, "Deep equality: different instances, same content");
      assertEquals(eq1.hashCode(), eq2.hashCode(), "Deep equality: hash codes same");

      assertNotEquals(eq1, eq3, "Deep inequality: different content");
      assertNotEquals(eq1, eq4, "Deep inequality: different length");
    }

    @Test
    void testIntArrayShallowEquality() {
      // Content-wise equal, different instance

      int[] array = {1, 2, 3};

      // Same instance
      var eq1 = createEquivalence(array, EqualityDeepness.SHALLOW_EQUAL);
      var eq2 = createEquivalence(array, EqualityDeepness.SHALLOW_EQUAL);

      // Different instances
      var eq3 = createEquivalence(new int[] {1, 2, 3}, EqualityDeepness.SHALLOW_EQUAL);

      assertEquals(eq1, eq2, "Shallow equality: same instance");
      assertEquals(
          eq1.hashCode(), eq2.hashCode(), "Shallow equality: hash codes same for same instance");

      assertNotEquals(eq1, eq3, "Shallow inequality: different instances, same content");
      assertNotEquals(
          eq1.hashCode(),
          eq3.hashCode(),
          "Shallow inequality: hash codes different for different instances");
    }

    @Test
    void testEmptyIntArrayDeepEquality() {
      var eqEmpty1 = createEquivalence(new int[] {}, EqualityDeepness.DEEP_EQUAL);
      var eqEmpty2 = createEquivalence(new int[] {}, EqualityDeepness.DEEP_EQUAL);

      assertEquals(eqEmpty1, eqEmpty2);
      assertEquals(eqEmpty1.hashCode(), eqEmpty2.hashCode());
    }
  }

  @Nested
  @DisplayName("Tests for long[] IDs")
  class LongArrayIdTests {
    @Test
    void testLongArrayDeepEquality() {
      var eq1 = createEquivalence(new long[] {1L, 2L, 3L}, EqualityDeepness.DEEP_EQUAL);
      var eq2 = createEquivalence(new long[] {1L, 2L, 3L}, EqualityDeepness.DEEP_EQUAL);
      var eq3 = createEquivalence(new long[] {4L, 5L, 6L}, EqualityDeepness.DEEP_EQUAL);

      assertEquals(eq1, eq2);
      assertEquals(eq1.hashCode(), eq2.hashCode());
      assertNotEquals(eq1, eq3);
    }

    @Test
    void testLongArrayShallowEquality() {
      var eq1 = createEquivalence(new long[] {1L, 2L, 3L}, EqualityDeepness.SHALLOW_EQUAL);
      var eq2 = createEquivalence(new long[] {1L, 2L, 3L}, EqualityDeepness.SHALLOW_EQUAL);

      assertNotEquals(eq1, eq2);
      assertNotEquals(eq1.hashCode(), eq2.hashCode());
    }

    @Test
    void testEmptyLongArrayDeepEquality() {
      var eqEmpty1 = createEquivalence(new long[] {}, EqualityDeepness.DEEP_EQUAL);
      var eqEmpty2 = createEquivalence(new long[] {}, EqualityDeepness.DEEP_EQUAL);

      assertEquals(eqEmpty1, eqEmpty2);
      assertEquals(eqEmpty1.hashCode(), eqEmpty2.hashCode());
    }
  }

  @Nested
  @DisplayName("Tests for byte[] IDs")
  class ByteArrayIdTests {
    @Test
    void testByteArrayDeepEquality() {
      var eq1 = createEquivalence(new byte[] {1, 2, 3}, EqualityDeepness.DEEP_EQUAL);
      var eq2 = createEquivalence(new byte[] {1, 2, 3}, EqualityDeepness.DEEP_EQUAL);
      var eq3 = createEquivalence(new byte[] {4, 5, 6}, EqualityDeepness.DEEP_EQUAL);

      assertEquals(eq1, eq2);
      assertEquals(eq1.hashCode(), eq2.hashCode());
      assertNotEquals(eq1, eq3);
    }

    @Test
    void testByteArrayShallowEquality() {
      var eq1 = createEquivalence(new byte[] {1, 2, 3}, EqualityDeepness.SHALLOW_EQUAL);
      var eq2 = createEquivalence(new byte[] {1, 2, 3}, EqualityDeepness.SHALLOW_EQUAL);

      assertNotEquals(eq1, eq2);
      assertNotEquals(eq1.hashCode(), eq2.hashCode());
    }

    @Test
    void testEmptyByteArrayDeepEquality() {
      var eqEmpty1 = createEquivalence(new byte[] {}, EqualityDeepness.DEEP_EQUAL);
      var eqEmpty2 = createEquivalence(new byte[] {}, EqualityDeepness.DEEP_EQUAL);

      assertEquals(eqEmpty1, eqEmpty2);
      assertEquals(eqEmpty1.hashCode(), eqEmpty2.hashCode());
    }
  }

  @Nested
  @DisplayName("Tests for short[] IDs")
  class ShortArrayIdTests {
    @Test
    void testShortArrayDeepEquality() {

      var eq1 = createEquivalence(new short[] {1, 2, 3}, EqualityDeepness.DEEP_EQUAL);
      var eq2 = createEquivalence(new short[] {1, 2, 3}, EqualityDeepness.DEEP_EQUAL);
      var eq3 = createEquivalence(new short[] {4, 5, 6}, EqualityDeepness.DEEP_EQUAL);

      assertEquals(eq1, eq2);
      assertEquals(eq1.hashCode(), eq2.hashCode());
      assertNotEquals(eq1, eq3);
    }

    @Test
    void testShortArrayShallowEquality() {
      var eq1 = createEquivalence(new short[] {1, 2, 3}, EqualityDeepness.SHALLOW_EQUAL);
      var eq2 = createEquivalence(new short[] {1, 2, 3}, EqualityDeepness.SHALLOW_EQUAL);

      assertNotEquals(eq1, eq2);
      assertNotEquals(eq1.hashCode(), eq2.hashCode());
    }

    @Test
    void testEmptyShortArrayDeepEquality() {
      var eqEmpty1 = createEquivalence(new short[] {}, EqualityDeepness.DEEP_EQUAL);
      var eqEmpty2 = createEquivalence(new short[] {}, EqualityDeepness.DEEP_EQUAL);

      assertEquals(eqEmpty1, eqEmpty2);
      assertEquals(eqEmpty1.hashCode(), eqEmpty2.hashCode());
    }
  }

  @Nested
  @DisplayName("Tests for boolean[] IDs")
  class BooleanArrayIdTests {
    @Test
    void testBooleanArrayDeepEquality() {
      var eq1 = createEquivalence(new boolean[] {true, false, true}, EqualityDeepness.DEEP_EQUAL);
      var eq2 = createEquivalence(new boolean[] {true, false, true}, EqualityDeepness.DEEP_EQUAL);
      var eq3 = createEquivalence(new boolean[] {false, true, false}, EqualityDeepness.DEEP_EQUAL);

      assertEquals(eq1, eq2);
      assertEquals(eq1.hashCode(), eq2.hashCode());
      assertNotEquals(eq1, eq3);
    }

    @Test
    void testBooleanArrayShallowEquality() {
      var eq1 =
          createEquivalence(new boolean[] {true, false, true}, EqualityDeepness.SHALLOW_EQUAL);
      var eq2 =
          createEquivalence(new boolean[] {true, false, true}, EqualityDeepness.SHALLOW_EQUAL);

      assertNotEquals(eq1, eq2);
      assertNotEquals(eq1.hashCode(), eq2.hashCode());
    }

    @Test
    void testEmptyBooleanArrayDeepEquality() {
      var eqEmpty1 = createEquivalence(new boolean[] {}, EqualityDeepness.DEEP_EQUAL);
      var eqEmpty2 = createEquivalence(new boolean[] {}, EqualityDeepness.DEEP_EQUAL);

      assertEquals(eqEmpty1, eqEmpty2);
      assertEquals(eqEmpty1.hashCode(), eqEmpty2.hashCode());
    }
  }

  @Nested
  @DisplayName("Tests for char[] IDs")
  class CharArrayIdTests {
    @Test
    void testCharArrayDeepEquality() {

      var eq1 = createEquivalence(new char[] {'a', 'b', 'c'}, EqualityDeepness.DEEP_EQUAL);
      var eq2 = createEquivalence(new char[] {'a', 'b', 'c'}, EqualityDeepness.DEEP_EQUAL);
      var eq3 = createEquivalence(new char[] {'x', 'y', 'z'}, EqualityDeepness.DEEP_EQUAL);

      assertEquals(eq1, eq2);
      assertEquals(eq1.hashCode(), eq2.hashCode());
      assertNotEquals(eq1, eq3);
    }

    @Test
    void testCharArrayShallowEquality() {
      var eq1 = createEquivalence(new char[] {'a', 'b', 'c'}, EqualityDeepness.SHALLOW_EQUAL);
      var eq2 = createEquivalence(new char[] {'a', 'b', 'c'}, EqualityDeepness.SHALLOW_EQUAL);

      assertNotEquals(eq1, eq2);
      assertNotEquals(eq1.hashCode(), eq2.hashCode());
    }

    @Test
    void testEmptyCharArrayDeepEquality() {
      var eqEmpty1 = createEquivalence(new char[] {}, EqualityDeepness.DEEP_EQUAL);
      var eqEmpty2 = createEquivalence(new char[] {}, EqualityDeepness.DEEP_EQUAL);

      assertEquals(eqEmpty1, eqEmpty2);
      assertEquals(eqEmpty1.hashCode(), eqEmpty2.hashCode());
    }
  }

  @Nested
  @DisplayName("Tests for double[] IDs")
  class DoubleArrayIdTests {
    @Test
    void testDoubleArrayDeepEquality() {
      var eq1 = createEquivalence(new double[] {1.0, 2.5, 3.14}, EqualityDeepness.DEEP_EQUAL);
      var eq2 = createEquivalence(new double[] {1.0, 2.5, 3.14}, EqualityDeepness.DEEP_EQUAL);
      var eq3 = createEquivalence(new double[] {4.0, 5.5, 6.28}, EqualityDeepness.DEEP_EQUAL);

      assertEquals(eq1, eq2);
      assertEquals(eq1.hashCode(), eq2.hashCode());
      assertNotEquals(eq1, eq3);
    }

    @Test
    void testDoubleArrayShallowEquality() {
      var eq1 = createEquivalence(new double[] {1.0, 2.5, 3.14}, EqualityDeepness.SHALLOW_EQUAL);
      var eq2 = createEquivalence(new double[] {1.0, 2.5, 3.14}, EqualityDeepness.SHALLOW_EQUAL);

      assertNotEquals(eq1, eq2);
      assertNotEquals(eq1.hashCode(), eq2.hashCode());
    }

    @Test
    void testEmptyDoubleArrayDeepEquality() {
      var eq1 = createEquivalence(new double[] {}, EqualityDeepness.DEEP_EQUAL);
      var eq2 = createEquivalence(new double[] {}, EqualityDeepness.DEEP_EQUAL);

      assertEquals(eq1, eq2);
      assertEquals(eq1.hashCode(), eq2.hashCode());
    }

    @Test
    void testDoubleArraySpecialValuesDeepEquality() {

      // Arrays.equals treats NaNs as equal
      // Arrays.equals treats 0.0 and -0.0 as equal
      var eqNaN1 =
          createEquivalence(new double[] {1.0, Double.NaN, 2.0}, EqualityDeepness.DEEP_EQUAL);
      var eqNaN2 =
          createEquivalence(new double[] {1.0, Double.NaN, 2.0}, EqualityDeepness.DEEP_EQUAL);
      var eqZero1 = createEquivalence(new double[] {0.0, 1.0}, EqualityDeepness.DEEP_EQUAL);
      var eqNegZero1 = createEquivalence(new double[] {-0.0, 1.0}, EqualityDeepness.DEEP_EQUAL);

      assertEquals(eqNaN1, eqNaN2, "NaNs in same position should be deep equal");
      assertEquals(
          eqNaN1.hashCode(),
          eqNaN2.hashCode(),
          "Hashcodes for arrays with NaNs should be consistent");

      assertNotEquals(
          eqZero1.hashCode(),
          eqNegZero1.hashCode(),
          "Hashcodes for 0.0 and -0.0 are different due to Arrays.hashCode behavior, "
              + "highlighting an equals/hashCode contract violation for this specific case.");
    }
  }

  @Nested
  @DisplayName("Tests for float[] IDs")
  class FloatArrayIdTests {
    @Test
    void testFloatArrayDeepEquality() {
      var eq1 = createEquivalence(new float[] {1.0f, 2.5f, 3.14f}, EqualityDeepness.DEEP_EQUAL);
      var eq2 = createEquivalence(new float[] {1.0f, 2.5f, 3.14f}, EqualityDeepness.DEEP_EQUAL);
      var eq3 = createEquivalence(new float[] {4.0f, 5.5f, 6.28f}, EqualityDeepness.DEEP_EQUAL);

      assertEquals(eq1, eq2);
      assertEquals(eq1.hashCode(), eq2.hashCode());
      assertNotEquals(eq1, eq3);
    }

    @Test
    void testFloatArrayShallowEquality() {
      var eq1 = createEquivalence(new float[] {1.0f, 2.5f, 3.14f}, EqualityDeepness.SHALLOW_EQUAL);
      var eq2 = createEquivalence(new float[] {1.0f, 2.5f, 3.14f}, EqualityDeepness.SHALLOW_EQUAL);

      assertNotEquals(eq1, eq2);
      assertNotEquals(eq1.hashCode(), eq2.hashCode());
    }

    @Test
    void testEmptyFloatArrayDeepEquality() {
      var eqEmpty1 = createEquivalence(new float[] {}, EqualityDeepness.DEEP_EQUAL);
      var eqEmpty2 = createEquivalence(new float[] {}, EqualityDeepness.DEEP_EQUAL);

      assertEquals(eqEmpty1, eqEmpty2);
      assertEquals(eqEmpty1.hashCode(), eqEmpty2.hashCode());
    }

    @Test
    void testFloatArraySpecialValuesDeepEquality() {
      // Arrays.equals treats NaNs as equal
      // Arrays.equals treats 0.0f and -0.0f as equal
      var eqNaN1 =
          createEquivalence(new float[] {1.0f, Float.NaN, 2.0f}, EqualityDeepness.DEEP_EQUAL);
      var eqNaN2 =
          createEquivalence(new float[] {1.0f, Float.NaN, 2.0f}, EqualityDeepness.DEEP_EQUAL);
      var eqZero1 = createEquivalence(new float[] {0.0f, 1.0f}, EqualityDeepness.DEEP_EQUAL);
      var eqNegZero1 = createEquivalence(new float[] {-0.0f, 1.0f}, EqualityDeepness.DEEP_EQUAL);

      assertEquals(eqNaN1, eqNaN2, "NaNs in same position should be deep equal");

      assertEquals(
          eqNaN1.hashCode(),
          eqNaN2.hashCode(),
          "Hashcodes for arrays with NaNs should be consistent");

      /*
       * IMPORTANT: This assertion reflects the current behavior of using Arrays.hashCode for
       * float[] which produces different hash codes for 0.0f and -0.0f, while Arrays.equals
       * (used by deepEquals) treats them as equal. This means the equals/hashCode contract
       * is violated for this specific case.
       */
      assertNotEquals(
          eqZero1.hashCode(),
          eqNegZero1.hashCode(),
          "Hashcodes for 0.0f and -0.0f are different due to Arrays.hashCode behavior, "
              + "highlighting an equals/hashCode contract violation for this specific case.");
    }
  }
}
