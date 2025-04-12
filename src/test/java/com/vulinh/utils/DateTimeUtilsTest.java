package com.vulinh.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.time.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class DateTimeUtilsTest {

  static final ZoneId BANGKOK_ZONE = ZoneId.of("Asia/Bangkok");

  static final Instant FIXED_INSTANT = Instant.ofEpochMilli(1698377400000L); // 2023-10-27T03:30:00Z
  static final LocalDateTime FIXED_LOCAL_DATE_TIME = LocalDateTime.of(2023, 10, 27, 10, 30, 0);
  static final ZoneId BERLIN_TIMEZONE = ZoneId.of("Europe/Berlin");

  MockedStatic<ZoneId> mockedZoneId;

  @BeforeEach
  void setUp() {
    mockedZoneId = Mockito.mockStatic(ZoneId.class, Mockito.CALLS_REAL_METHODS);
    mockedZoneId.when(ZoneId::systemDefault).thenReturn(BANGKOK_ZONE);
  }

  @AfterEach
  void tearDown() {
    if (mockedZoneId != null) {
      mockedZoneId.close();
    }
  }

  @Test
  void testToInstant() {
    var actual = DateTimeUtils.toInstant(FIXED_LOCAL_DATE_TIME);

    assertEquals(FIXED_INSTANT, actual);
  }

  @Test
  void testToLocalDateTime() {
    var actual = DateTimeUtils.toLocalDateTime(FIXED_INSTANT);

    assertEquals(FIXED_LOCAL_DATE_TIME, actual);
  }

  @Test
  void testToInstantExtended01() {
    var expected = Instant.parse("2024-05-10T12:35:27.000Z");

    // Convert Berlin local time (with DST) to UTC:
    // On May 10, 2024, Berlin observes DST (CEST, UTC+2).
    // Thus, 14:35:27 in Berlin corresponds to 12:35:27 UTC.
    var actual =
        DateTimeUtils.toInstant(LocalDateTime.of(2024, 5, 10, 14, 35, 27), BERLIN_TIMEZONE, null);

    assertEquals(expected, actual);
  }

  @Test
  void testToInstantExtended02() {
    var expected = Instant.parse("2024-11-25T16:45:30.000Z");

    // Convert Berlin local time (without DST) to UTC:
    // On November 25, 2024, Berlin is on standard time (CET, UTC+1).
    // Thus, 17:45:30 in Berlin corresponds to 16:45:30 UTC.
    var actual =
        DateTimeUtils.toInstant(LocalDateTime.of(2024, 11, 25, 17, 45, 30), BERLIN_TIMEZONE, null);

    assertEquals(expected, actual);
  }

  @Test
  void testToLocalDateExtended01() {
    // Convert 2024-05-10T12:35:27Z (UTC) to Berlin local time.
    // In May, Berlin is observing DST (CEST, UTC+2),
    // so the local time becomes 2024-05-10T14:35:27.
    var expected = LocalDateTime.parse("2024-05-10T14:35:27.000");

    var actual =
        DateTimeUtils.toLocalDateTime(
            Instant.parse("2024-05-10T12:35:27.000Z"), BERLIN_TIMEZONE, null);

    assertEquals(expected, actual);
  }

  @Test
  void testToLocalDateExtended02() {
    // Convert 2024-12-25T23:35:48Z (UTC) to Berlin local time.
    // In December, Berlin is on standard time (CET, UTC+1),
    // so the local time becomes 2024-12-26T00:35:48.
    var expected = LocalDateTime.parse("2024-12-26T00:35:48.000");

    var actual =
        DateTimeUtils.toLocalDateTime(
            Instant.parse("2024-12-25T23:35:48.000Z"), BERLIN_TIMEZONE, null);

    assertEquals(expected, actual);
  }

  @Test
  void testWithCustomConverter01() {
    try (var mockedLocalTime = Mockito.mockStatic(LocalTime.class, Mockito.CALLS_REAL_METHODS)) {
      mockedLocalTime.when(LocalTime::now).thenReturn(LocalTime.MIDNIGHT);

      var expected = LocalDateTime.parse("2025-02-18T00:00:00.000");

      var actual =
          DateTimeUtils.toLocalDateTime(
              LocalDate.of(2025, 2, 18),
              null,
              (ld, ignored) -> LocalDateTime.of(ld, LocalTime.now()));

      assertEquals(expected, actual);
    }
  }

  @Test
  void testWithCustomConverter02() {
    // It has to be outside (or maybe finality of some sorts), otherwise,
    // org.mockito.exceptions.misusing.UnfinishedStubbingException will be thrown
    var mockedNowDate = LocalDate.of(2025, 2, 18);

    try (var mockedLocalDate = Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
      mockedLocalDate.when(LocalDate::now).thenReturn(mockedNowDate);

      var expected = LocalDateTime.parse("2025-02-18T14:23:38.000");

      var actual =
          DateTimeUtils.toLocalDateTime(
              LocalTime.of(14, 23, 38),
              null,
              (lt, ignored) -> LocalDateTime.of(LocalDate.now(), lt));

      assertEquals(expected, actual);
    }
  }

  @Test
  void testWithCustomConverter03() {
    assertThrows(
        Exception.class, () -> DateTimeUtils.toLocalDateTime(LocalTime.of(14, 2, 18), null, null));
  }
}
