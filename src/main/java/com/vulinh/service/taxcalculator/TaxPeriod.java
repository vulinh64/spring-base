package com.vulinh.service.taxcalculator;

import module java.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum TaxPeriod {
  PRE_2026(11_000_000D, 4_400_000D, ProgressiveTaxLevel.PRE_2026_LEVEL),
  POST_2026(15_500_000D, 6_200_000D, ProgressiveTaxLevel.POST_2026_LEVEL);

  final double personalDeduction;
  final double dependantDeduction;
  final List<ProgressiveTaxLevel> progressiveTaxLevel;

  record ProgressiveTaxLevel(double threshold, double rate) {

    public static final List<ProgressiveTaxLevel> PRE_2026_LEVEL =
        List.of(
            ProgressiveTaxLevel.of(0.0, 0.0),
            ProgressiveTaxLevel.of(5_000_000.0, 0.05),
            ProgressiveTaxLevel.of(10_000_000.0, 0.10),
            ProgressiveTaxLevel.of(18_000_000.0, 0.15),
            ProgressiveTaxLevel.of(32_000_000.0, 0.2),
            ProgressiveTaxLevel.of(52_000_000.0, 0.25),
            ProgressiveTaxLevel.of(80_000_000.0, 0.3),
            ProgressiveTaxLevel.of(Double.MAX_VALUE, 0.35));

    public static final List<ProgressiveTaxLevel> POST_2026_LEVEL =
        List.of(
            ProgressiveTaxLevel.of(0.0, 0.0),
            ProgressiveTaxLevel.of(10_000_000.0, 0.05),
            ProgressiveTaxLevel.of(30_000_000.0, 0.15),
            ProgressiveTaxLevel.of(60_000_000.0, 0.25),
            ProgressiveTaxLevel.of(100_000_000.0, 0.3),
            ProgressiveTaxLevel.of(Double.MAX_VALUE, 0.35));

    private static ProgressiveTaxLevel of(double threshold, double rate) {
      return new ProgressiveTaxLevel(threshold, rate);
    }
  }
}
