package com.vulinh.utils;

import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.function.BiFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeUtils {

  /**
   * Javadoc kindly provided by ChatGPT!
   *
   * <p>Converts a given {@link TemporalAccessor} to an {@link Instant}, using an optional time zone
   * and a custom conversion strategy.
   *
   * <p>This method supports popular temporal types such as {@link LocalDateTime}, {@link
   * OffsetDateTime}, {@link ZonedDateTime}, and {@link Instant}. If {@code temporalValue} is {@code
   * null}, the method returns {@code null} immediately.
   *
   * <p>If a custom {@code converter} is provided, it is used to perform the conversion. Otherwise,
   * a default conversion strategy is applied based on the runtime type of {@code temporalValue}.
   *
   * <p>If {@code inputZone} is {@code null}, the system default time zone is used via {@link
   * ZoneId#systemDefault()}. Be aware that this may not reflect changes to the system's time zone
   * after the application has started.
   *
   * @param temporalValue the temporal value to convert; may be {@code null}
   * @param inputZone the time zone to apply during conversion; if {@code null}, the system default
   *     is used
   * @param converter an optional custom converter function for the conversion logic
   * @param <T> the type of the temporal value, extending {@link TemporalAccessor}
   * @return the resulting {@code Instant}, or {@code null} if {@code temporalValue} is {@code null}
   * @throws IllegalArgumentException if the temporal value type is unsupported and no custom
   *     converter is provided
   */
  @Nullable
  public static <T extends TemporalAccessor> Instant toInstant(
      @Nullable T temporalValue,
      @Nullable ZoneId inputZone,
      @Nullable BiFunction<T, ZoneId, Instant> converter) {
    if (temporalValue == null) {
      return null;
    }

    // Use the provided zone if available; otherwise fallback to system default
    var actualZone = inputZone != null ? inputZone : ZoneId.systemDefault();

    // Use the custom converter if one is provided
    if (converter != null) {
      return converter.apply(temporalValue, actualZone);
    }

    // Default conversion logic based on temporal type
    return switch (temporalValue) {
      case LocalDateTime ldt -> ldt.atZone(actualZone).toInstant();
      case OffsetDateTime odt -> odt.toInstant();
      case ZonedDateTime zdt -> zdt.toInstant();
      case Instant i -> i;
      default ->
          throw new IllegalArgumentException(
              "Unsupported temporal type: " + temporalValue.getClass().getName());
    };
  }

  /**
   * Javadoc kindly provided by ChatGPT!
   *
   * <p>Converts a given {@link TemporalAccessor} to a {@link LocalDateTime}, using an optional time
   * zone and a custom conversion strategy.
   *
   * <p>This method supports popular temporal types such as {@link LocalDateTime}, {@link
   * OffsetDateTime}, {@link ZonedDateTime}, and {@link Instant}. If {@code temporalValue} is {@code
   * null}, the method returns {@code null} immediately.
   *
   * <p>If a custom {@code converter} is provided, it is used to perform the conversion. Otherwise,
   * a default conversion is attempted based on the runtime type of {@code temporalValue}.
   *
   * <p>If {@code inputZone} is {@code null}, the system default time zone (via {@link
   * ZoneId#systemDefault()}) is used. Note that this may not reflect changes to the system time
   * zone made during runtime.
   *
   * @param temporalValue the temporal value to convert; may be {@code null}
   * @param inputZone the time zone to apply during conversion; if {@code null}, the system default
   *     is used
   * @param converter an optional custom converter function to perform the conversion
   * @param <T> the type of the temporal value, extending {@link TemporalAccessor}
   * @return the resulting {@code LocalDateTime}, or {@code null} if {@code temporalValue} is {@code
   *     null}
   * @throws IllegalArgumentException if the temporal value type is unsupported and no custom
   *     converter is provided
   */
  @Nullable
  public static <T extends TemporalAccessor> LocalDateTime toLocalDateTime(
      @Nullable T temporalValue,
      @Nullable ZoneId inputZone,
      @Nullable BiFunction<T, ZoneId, LocalDateTime> converter) {
    if (temporalValue == null) {
      return null;
    }

    // Use the provided zone if available, otherwise default to the system time zone
    var actualZone = inputZone != null ? inputZone : ZoneId.systemDefault();

    // Use custom converter if provided
    if (converter != null) {
      return converter.apply(temporalValue, actualZone);
    }

    // Apply default conversion based on the runtime type of the input value
    return switch (temporalValue) {
      case LocalDateTime ldt -> ldt;
      case OffsetDateTime odt -> odt.toLocalDateTime();
      case ZonedDateTime zdt -> zdt.toLocalDateTime();
      case Instant i -> i.atZone(actualZone).toLocalDateTime();
      default ->
          throw new IllegalArgumentException(
              "Unsupported temporal type: " + temporalValue.getClass().getName());
    };
  }

  @Nullable
  public static Instant toInstant(LocalDateTime localDateTime) {
    return toInstant(localDateTime, null, null);
  }

  @Nullable
  public static LocalDateTime toLocalDateTime(Instant instant) {
    return toLocalDateTime(instant, null, null);
  }
}
