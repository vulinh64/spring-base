package com.vulinh.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.vulinh.utils.Equivalence.Creator;
import com.vulinh.utils.Equivalence.EqualityDeepness;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EquivalenceStreamCreatorTest {

  // --- Helper Records/Classes for testing ---
  record Person(Integer id, String name) {}

  record ItemWithArrayId(int[] id, String description) {
    // Custom equals/hashCode for clarity in test assertions if we compare ItemWithArrayId instances
    // directly, though Equivalence's distinctness relies on its own ID-based comparison.
    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      var that = (ItemWithArrayId) o;

      return Arrays.equals(id, that.id) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
      var result = Objects.hash(description);
      result = 31 * result + Arrays.hashCode(id);
      return result;
    }
  }

  record ItemWithStringId(String id, String name) {}

  abstract static class Animal {
    abstract Integer id();

    abstract String name();

    // Basic equals/hashCode for list assertions
    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }

      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }

      var other = (Animal) obj;

      return Objects.equals(id(), other.id()) && Objects.equals(name(), other.name());
    }

    @Override
    public int hashCode() {
      return Objects.hash(id(), name());
    }
  }

  static class Dog extends Animal {
    private final Integer id;
    private final String name;

    public Dog(Integer id, String name) {
      this.id = id;
      this.name = name;
    }

    @Override
    Integer id() {
      return id;
    }

    @Override
    String name() {
      return name;
    }

    @Override
    public String toString() {
      return "Dog{id=%d, name='%s'}".formatted(id, name);
    }
  }

  static class Cat extends Animal {
    private final Integer id;
    private final String name;

    public Cat(Integer id, String name) {
      this.id = id;
      this.name = name;
    }

    @Override
    Integer id() {
      return id;
    }

    @Override
    String name() {
      return name;
    }

    @Override
    public String toString() {
      return "Cat{id=%d, name='%s'}".formatted(id, name);
    }
  }

  @Nested
  @DisplayName("buildDistinctEquivalenceStream tests")
  class BuildDistinctEquivalenceStreamTests {

    @Test
    @DisplayName("should return unique elements based on ID")
    void testBuildDistinct01() {
      var p1a = new Person(1, "Alice");
      var p2 = new Person(2, "Bob");
      var p1c = new Person(1, "Charlie"); // Same ID as p1a
      var p3 = new Person(3, "David");

      var persons = List.of(p1a, p2, p1c, p3, p2); // p1c has same ID as p1a, p2 is duplicated

      var distinctPersons =
          Creator.buildDistinctEquivalenceStream(
                  persons, Person::id, EqualityDeepness.SHALLOW_EQUAL)
              .toList();

      // Stream.distinct() is stable, so it should keep the first occurrence.
      assertEquals(List.of(p1a, p2, p3), distinctPersons);
      assertEquals(3, distinctPersons.size());
      assertTrue(distinctPersons.contains(p1a)); // or p1c, but p1a came first
      assertTrue(distinctPersons.contains(p2));
      assertTrue(distinctPersons.contains(p3));
      assertFalse(
          distinctPersons.contains(p1c) && distinctPersons.getFirst().equals(p1a)
              || !distinctPersons.contains(p1a)); // ensure only one of p1a/p1c
    }

    @Test
    @DisplayName("should return an empty stream for an empty collection")
    void testBuildDistinct02() {
      var emptyList = Collections.<Person>emptyList();

      var distinctPersons =
          Creator.buildDistinctEquivalenceStream(
                  emptyList, Person::id, EqualityDeepness.SHALLOW_EQUAL)
              .toList();

      assertTrue(distinctPersons.isEmpty());
    }

    @Test
    @DisplayName("should return all elements if all are unique by ID")
    void testBuildDistinct03() {
      var p1 = new Person(1, "Alice");
      var p2 = new Person(2, "Bob");
      var p3 = new Person(3, "Charlie");

      var persons = List.of(p1, p2, p3);

      var distinctPersons =
          Creator.buildDistinctEquivalenceStream(
                  persons, Person::id, EqualityDeepness.SHALLOW_EQUAL)
              .toList();

      assertEquals(persons, distinctPersons);
      assertEquals(3, distinctPersons.size());
    }

    @Test
    @DisplayName("should filter out null elements from the collection")
    void testBuildDistinct04() {
      var p1a = new Person(1, "Alice");
      var p2 = new Person(2, "Bob");
      var p1c = new Person(1, "Charlie"); // Same ID as p1a

      var persons = new ArrayList<Person>();

      persons.add(p1a);
      persons.add(null);
      persons.add(p2);
      persons.add(null);
      persons.add(p1c);

      var distinctPersons =
          Creator.buildDistinctEquivalenceStream(
                  persons, Person::id, EqualityDeepness.SHALLOW_EQUAL)
              .toList();

      assertEquals(List.of(p1a, p2), distinctPersons);
      assertEquals(2, distinctPersons.size());
    }

    @Test
    @DisplayName("should filter out elements with null IDs")
    void testBuildDistinct05() {
      var p1 = new Person(1, "Alice");
      var pNullId = new Person(null, "NoID");
      var p2 = new Person(2, "Bob");

      var persons = List.of(p1, pNullId, p2, new Person(null, "NoID2"));

      var distinctPersons =
          Creator.buildDistinctEquivalenceStream(
                  persons, Person::id, EqualityDeepness.SHALLOW_EQUAL)
              .toList();

      assertEquals(List.of(p1, p2), distinctPersons);
      assertEquals(2, distinctPersons.size());
    }

    @Test
    @DisplayName("should filter out elements with blank string IDs")
    void testBuildDistinct06() {
      var item1 = new ItemWithStringId("ID1", "Item 1");
      var itemBlank = new ItemWithStringId("   ", "Item Blank"); // Blank ID
      var itemEmpty = new ItemWithStringId("", "Item Empty"); // Empty ID
      var item2 = new ItemWithStringId("ID2", "Item 2");
      var itemNullId = new ItemWithStringId(null, "Item Null ID");

      var items = List.of(item1, itemBlank, item2, itemEmpty, itemNullId);

      var distinctItems =
          Creator.buildDistinctEquivalenceStream(
                  items, ItemWithStringId::id, EqualityDeepness.SHALLOW_EQUAL)
              .toList();

      assertEquals(List.of(item1, item2), distinctItems);
      assertEquals(2, distinctItems.size());
    }

    @Test
    @DisplayName("should treat different array ID instances as different with SHALLOW_EQUAL")
    void testBuildDistinct07() {
      var itemA = new ItemWithArrayId(new int[] {1, 2}, "Item A");
      var itemB =
          new ItemWithArrayId(new int[] {1, 2}, "Item B"); // Same content, different instance

      var items = List.of(itemA, itemB);

      var distinctItems =
          Creator.buildDistinctEquivalenceStream(
                  items, ItemWithArrayId::id, EqualityDeepness.SHALLOW_EQUAL)
              .toList();

      assertEquals(List.of(itemA, itemB), distinctItems);
      assertEquals(2, distinctItems.size());
    }

    @Test
    @DisplayName("should treat array IDs with same content as equal with DEEP_EQUAL")
    void testBuildDistinct08() {
      var itemA = new ItemWithArrayId(new int[] {1, 2}, "Item A");
      var itemB =
          new ItemWithArrayId(new int[] {1, 2}, "Item B"); // Same content, different instance
      var itemC = new ItemWithArrayId(new int[] {3, 4}, "Item C");

      var items = List.of(itemA, itemB, itemC);

      var distinctItems =
          Creator.buildDistinctEquivalenceStream(
                  items, ItemWithArrayId::id, EqualityDeepness.DEEP_EQUAL)
              .toList();

      assertEquals(List.of(itemA, itemC), distinctItems); // itemB is duplicate of itemA by ID
      assertEquals(2, distinctItems.size());
    }

    @Test
    @DisplayName("should handle collections of subtypes correctly")
    void testBuildDistinct09() {
      var dog1 = new Dog(1, "Buddy");
      var cat1 = new Cat(2, "Whiskers");
      var dog1Dup = new Dog(1, "Max"); // Same ID as dog1
      var cat2 = new Cat(3, "Shadow");

      var distinctAnimals =
          Creator.buildDistinctEquivalenceStream(
                  List.of(dog1, cat1, dog1Dup, cat2), Animal::id, EqualityDeepness.SHALLOW_EQUAL)
              .toList();

      assertEquals(List.of(dog1, cat1, cat2), distinctAnimals);
      assertEquals(3, distinctAnimals.size());
    }
  }
}
