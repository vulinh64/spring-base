package com.vulinh.utils.post;

import static org.junit.jupiter.api.Assertions.*;

import module java.base;

import org.junit.jupiter.api.Test;

class NoDashedUUIDGeneratorTest {

  @Test
  void createNonDashedUUID() {
    var expected = "1234567890abcdef1234567890abcdef";

    var actual =
        NoDashedUUIDGenerator.createNonDashedUUID(
            UUID.fromString("12345678-90ab-cdef-1234-567890abcdef"));

    assertEquals(expected, actual);
  }
}
