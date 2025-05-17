package com.vulinh.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.vulinh.data.dto.response.data.AuthorData;
import com.vulinh.utils.Equivalence.Creator;
import com.vulinh.utils.Equivalence.EqualityDeepness;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

class EquivalenceTest {

  @Test
  void testBasicEquality() {
    var id = UUID.randomUUID();

    var author1 = new AuthorData(id, "John Doe", "johndoe");
    var author2 = new AuthorData(id, "John Doe Jr.", "johndoe");

    var eq1 = Creator.of(author1, AuthorData::username);
    var eq2 = Creator.of(author2, AuthorData::username);

    assertEquals(eq1, eq2);
    assertEquals(eq1.hashCode(), eq2.hashCode());
  }

  @Test
  void testDifferentIds() {
    var eq1 =
        Creator.of(new AuthorData(UUID.randomUUID(), "John Doe", "johndoe"), AuthorData::username);

    var eq2 =
        Creator.of(
            new AuthorData(UUID.randomUUID(), "Jane Smith", "janesmith"), AuthorData::username);

    assertNotEquals(eq1, eq2);
    assertNotEquals(eq2, eq1);
  }

  @SuppressWarnings("AssertBetweenInconvertibleTypes")
  @Test
  void testDifferentValueObjectsButSameId() {
    var sharedId = UUID.randomUUID();

    var eq1 = Creator.of(new AuthorData(sharedId, "John Doe", "johndoe"), AuthorData::id);
    var eq2 = Creator.of(new Dummy("John Doe", sharedId), Dummy::getId);

    assertEquals(eq1.id(), eq2.id());

    assertNotEquals(eq1, eq2);
    assertNotEquals(eq2, eq1);
  }

  @Test
  void testEquivalenceWithDifferentObject() {
    var unexpected = new Object();

    var equivalence = Creator.of("Joh", Function.identity());

    assertNotEquals(unexpected, equivalence);
    assertNotEquals(equivalence, unexpected);
  }

  @Test
  void testSubclassing() {
    var sharedId = UUID.randomUUID();

    var eq1 = Creator.of(new Dummy("john", sharedId), Dummy::getId);
    var eq2 = Creator.of(new ExtendedDummy("james", sharedId, "ext"), ExtendedDummy::getId);
    var eq3 = Creator.of(new ExtendedDummy("hans", UUID.randomUUID(), "ext"), ExtendedDummy::getId);

    assertEquals(eq1, eq2);
    assertEquals(eq2, eq1);
    assertNotEquals(eq1, eq3);
    assertNotEquals(eq2, eq3);
    assertNotEquals(eq3, eq1);
    assertNotEquals(eq3, eq2);
  }

  @Test
  void testStreamDistinct() {
    var author1 = new AuthorData(UUID.randomUUID(), "John Doe", "johndoe");
    var author2 = new AuthorData(UUID.randomUUID(), "Another John", "johndoe");
    var author3 = new AuthorData(UUID.randomUUID(), "Jane Smith", "janesmith");

    var distinctAuthors =
        Stream.of(author1, author2, author3)
            .map(author -> Creator.of(author, AuthorData::username))
            .distinct()
            .map(Equivalence::unwrap)
            .toList();

    // Should only have two elements as author1 and author2 have the same username
    assertEquals(2, distinctAuthors.size());

    assertTrue(distinctAuthors.contains(author1));
    assertTrue(distinctAuthors.contains(author3));
  }

  @Test
  void testWithCustomIdExtractor() {
    var author1 = new AuthorData(UUID.randomUUID(), "John Doe", "johndoe");
    var author2 = new AuthorData(UUID.randomUUID(), "John Doe", "johndoe");

    // Custom ID extractor that combines username and first letter of fullName
    var customExtractor =
        new Function<AuthorData, String>() {
          @Override
          public String apply(AuthorData author) {
            return "%s-%s".formatted(author.username(), author.fullName().charAt(0));
          }
        };

    var eq1 = Creator.of(author1, customExtractor);
    var eq2 = Creator.of(author2, customExtractor);

    assertEquals(eq1, eq2);
    assertEquals("johndoe-J", eq1.id());
  }

  @Test
  @SuppressWarnings("AssertBetweenInconvertibleTypes")
  void testWithDeepEquality() {
    var obj1 = new DeepEquality<>(new Integer[] {1, 2, 3}, "Test1");
    var obj2 = new DeepEquality<>(new Integer[] {2, 3, 4}, "Test 2");
    var obj3 = new DeepEquality<>(new Integer[] {2, 3, 4}, "Test 3");
    var obj4 = new DeepEquality<>(new String[] {"a", "b", "c"}, "Test 4");
    var obj5 = new DeepEquality<>(new Object[] {}, "Test 5");

    var wrapper1 = Creator.of(obj1, DeepEquality::id, EqualityDeepness.DEEP_EQUAL);
    var wrapper2 = Creator.of(obj2, DeepEquality::id, EqualityDeepness.DEEP_EQUAL);
    var wrapper3 = Creator.of(obj3, DeepEquality::id, EqualityDeepness.DEEP_EQUAL);
    var wrapper4 = Creator.of(obj4, DeepEquality::id, EqualityDeepness.DEEP_EQUAL);
    var wrapper5 = Creator.of(obj5, DeepEquality::other, EqualityDeepness.DEEP_EQUAL);

    assertNotEquals(wrapper1, wrapper2);
    assertNotEquals(wrapper2, wrapper1);

    assertEquals(wrapper2, wrapper3);
    assertEquals(wrapper2, wrapper3);

    assertNotEquals(wrapper1, wrapper4);
    assertNotEquals(wrapper4, wrapper1);

    assertNotEquals(wrapper1, wrapper5);
    assertNotEquals(wrapper5, wrapper1);
  }

  public record DeepEquality<T>(T[] id, String other) {}

  @RequiredArgsConstructor
  @Getter
  public static class Dummy {

    final String name;
    final UUID id;
  }

  @Getter
  public static class ExtendedDummy extends Dummy {

    final String extended;

    public ExtendedDummy(String name, UUID id, String extended) {
      super(name, id);
      this.extended = extended;
    }
  }
}
