package com.vulinh.service.taxcalculator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
enum TaxConstant {
  NON_TAXABLE_INCOME(11_000_000),
  DEDUCTION_PER_DEPENDANTS(4_400_000);

  private final double value;
}
