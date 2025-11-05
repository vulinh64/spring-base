package com.vulinh.service.taxcalculator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum TaxPeriod {
  PRE_2026(11_000_000D, 4_400_000D),
  POST_2026(15_500_000D, 6_200_000D);

  final double personalDeduction;
  final double dependantDeduction;
}
