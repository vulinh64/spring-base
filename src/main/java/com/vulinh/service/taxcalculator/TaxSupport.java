package com.vulinh.service.taxcalculator;

import module java.base;

import com.vulinh.exception.ApplicationException;
import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaxSupport {

  static final int NEW_DEDUCTION_EFFECTIVE_YEAR = 2026;

  static final LocalDate NEW_PROGRESSIVE_LEVEL_EFFECTIVE_DATE = LocalDate.of(2026, Month.JULY, 1);

  @RequiredArgsConstructor
  @Getter
  @Accessors(fluent = true)
  enum ProbationRate {
    MIN_PERCENTAGE(0.85),
    MAX_PERCENTAGE(1.0),
    DEDUCTION_PERCENTAGE(0.1);

    final double percentage;
  }

  @RequiredArgsConstructor
  @Getter
  @Accessors(fluent = true)
  enum InsuranceRate {
    SOCIAL_INSURANCE(0.08),
    HEALTH_INSURANCE(0.015),
    UNEMPLOYMENT_INSURANCE(0.01);

    final double rate;
  }

  @RequiredArgsConstructor
  @Getter
  @Accessors(fluent = true)
  enum TaxConstant {
    MAX_BASIC_SALARY(46_800_000),
    MIN_BASIC_SALARY(5_100_000);

    final double value;
  }

  @RequiredArgsConstructor
  @Getter
  @Accessors(fluent = true)
  enum TaxDeductionPeriod {
    PRE_2026(11_000_000D, 4_400_000D),
    POST_2026(15_500_000D, 6_200_000D);

    final double personalDeduction;
    final double dependantDeduction;

    public static TaxDeductionPeriod fromYear(LocalDate date) {
      return date.getYear() < NEW_DEDUCTION_EFFECTIVE_YEAR ? PRE_2026 : POST_2026;
    }
  }

  @RequiredArgsConstructor
  @Getter
  @Accessors(fluent = true)
  enum ProgressiveTaxPeriod {
    PRE_2026_JUL(ProgressiveTaxLevel.PRE_2026_JUL_LEVEL),
    POST_2026_JUL(ProgressiveTaxLevel.POST_2026_JUL_LEVEL);

    final List<ProgressiveTaxLevel> levels;

    public static ProgressiveTaxPeriod fromDate(LocalDate date) {
      return date.isBefore(NEW_PROGRESSIVE_LEVEL_EFFECTIVE_DATE) ? PRE_2026_JUL : POST_2026_JUL;
    }
  }

  @Builder
  @With
  record InsuranceDTO(
      double socialInsurance,
      double healthInsurance,
      double unemploymentInsurance,
      double totalInsurance) {}

  @Builder
  @With
  record PersonalTaxDTO(
      double pretaxSalary,
      double taxableIncome,
      double deductedAmount,
      double taxAmount,
      List<Double> progressiveTaxLevels,
      double netIncome) {

    PersonalTaxDTO {
      progressiveTaxLevels =
          progressiveTaxLevels == null ? Collections.emptyList() : progressiveTaxLevels;
    }
  }

  record ProgressiveTaxLevel(double threshold, double rate) {

    static final ProgressiveTaxLevel ZERO_LEVEL = ProgressiveTaxLevel.of(0.0, 0.0);
    static final ProgressiveTaxLevel MAX_LEVEL = ProgressiveTaxLevel.of(Double.MAX_VALUE, 0.35);

    // Before 2026
    static final List<ProgressiveTaxLevel> PRE_2026_JUL_LEVEL =
        List.of(
            ZERO_LEVEL,
            ProgressiveTaxLevel.of(5_000_000.0, 0.05),
            ProgressiveTaxLevel.of(10_000_000.0, 0.10),
            ProgressiveTaxLevel.of(18_000_000.0, 0.15),
            ProgressiveTaxLevel.of(32_000_000.0, 0.2),
            ProgressiveTaxLevel.of(52_000_000.0, 0.25),
            ProgressiveTaxLevel.of(80_000_000.0, 0.3),
            MAX_LEVEL);

    // Starting from 2026
    static final List<ProgressiveTaxLevel> POST_2026_JUL_LEVEL =
        List.of(
            ZERO_LEVEL,
            ProgressiveTaxLevel.of(10_000_000.0, 0.05),
            ProgressiveTaxLevel.of(30_000_000.0, 0.15),
            ProgressiveTaxLevel.of(60_000_000.0, 0.25),
            ProgressiveTaxLevel.of(100_000_000.0, 0.3),
            MAX_LEVEL);

    static ProgressiveTaxLevel of(double threshold, double rate) {
      return new ProgressiveTaxLevel(threshold, rate);
    }
  }

  static class TaxCalculatorException extends ApplicationException {

    @Serial private static final long serialVersionUID = -4699666572519989228L;

    TaxCalculatorException(String message) {
      super(message, () -> "MXXXX");
    }
  }
}
