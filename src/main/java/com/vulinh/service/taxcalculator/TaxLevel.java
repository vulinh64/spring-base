package com.vulinh.service.taxcalculator;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum TaxLevel {
  LEVEL_0(0.0, 0.0),
  LEVEL_1(5_000_000.0, 0.05),
  LEVEL_2(10_000_000.0, 0.10),
  LEVEL_3(18_000_000.0, 0.15),
  LEVEL_4(32_000_000.0, 0.2),
  LEVEL_5(52_000_000.0, 0.25),
  LEVEL_6(80_000_000.0, 0.3),
  LEVEL_7(Double.MAX_VALUE, 0.35);

  private final double threshold;
  private final double rate;

  private static final Map<Integer, TaxLevel> MAPS =
      Arrays.stream(values()).collect(Collectors.toMap(TaxLevel::ordinal, Function.identity()));

  public static TaxLevel parseTaxLevel(int taxLevel) {
    return Optional.ofNullable(MAPS.get(taxLevel))
        .orElseThrow(() -> new IllegalArgumentException("taxLevel = %d".formatted(taxLevel)));
  }
}
