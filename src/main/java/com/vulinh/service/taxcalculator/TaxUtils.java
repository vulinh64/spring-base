package com.vulinh.service.taxcalculator;

import module java.base;

import com.google.common.collect.ImmutableMap;
import com.vulinh.service.taxcalculator.TaxRequestDTO.InsuranceDTO;
import com.vulinh.service.taxcalculator.TaxRequestDTO.PersonalTaxDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TaxUtils {

  public static PersonalTaxDTO calculatePersonalTaxProbation(TaxRequestDTO taxRequestDTO) {
    var probationPercentage = taxRequestDTO.probationPercentage();

    if (Double.compare(probationPercentage, ProbationRate.MIN_PERCENTAGE.percentage()) < 0
        || Double.compare(probationPercentage, ProbationRate.MAX_PERCENTAGE.percentage()) > 0) {
      throw new TaxRequestDTO.TaxCalculatorException("Invalid probation percentage");
    }

    var totalSalary = taxRequestDTO.totalSalary();
    var taxableAMount = totalSalary * taxRequestDTO.probationPercentage();
    var taxAmount = taxableAMount * ProbationRate.DEDUCTION_PERCENTAGE.percentage();

    return PersonalTaxDTO.builder()
        .pretaxSalary(totalSalary)
        .taxAmount(taxAmount)
        .taxableIncome(taxableAMount)
        .netIncome(taxableAMount - taxAmount)
        .build();
  }

  public static InsuranceDTO calculateInsurance(TaxRequestDTO taxRequestDTO) {
    var basicSalary = taxRequestDTO.basicSalary();

    var healthInsurance = basicSalary * InsuranceRate.HEALTH_INSURANCE.rate();

    var socialInsurance = basicSalary * InsuranceRate.SOCIAL_INSURANCE.rate();

    var unemploymentInsurance = basicSalary * InsuranceRate.UNEMPLOYMENT_INSURANCE.rate();

    return InsuranceDTO.builder()
        .healthInsurance(healthInsurance)
        .socialInsurance(socialInsurance)
        .unemploymentInsurance(unemploymentInsurance)
        .totalInsurance(healthInsurance + socialInsurance + unemploymentInsurance)
        .build();
  }

  public static PersonalTaxDTO calculatePersonalTax(
      TaxRequestDTO taxRequestDTO, InsuranceDTO insuranceDTO, TaxPeriod taxPeriod) {
    var totalSalary = taxRequestDTO.totalSalary();

    // Tổng đóng BH
    var totalInsurance = insuranceDTO.totalInsurance();

    // Thu nhập trước thuế = Tổng thu nhập - Bảo hiểm
    var pretaxSalary = totalSalary - totalInsurance;

    // Người phụ thuộc * Giảm trừ mỗi người phụ thuộc
    var dependantDeduction =
        taxPeriod.dependantDeduction() * taxRequestDTO.numberOfDependants();

    // Thu nhập chịu thuế = Thu nhập trước thuế - Thu nhập miễn thuế - Giảm trừ người phụ thuộc
    var taxableIncome =
        pretaxSalary - (taxPeriod.personalDeduction() + dependantDeduction);

    // Thu nhập chịu thuế luôn phải lớn hơn hoặc bằng 0
    if (taxableIncome < 0) {
      taxableIncome = 0.0;
    }

    // Thuế lũy tiến
    var progressiveTaxLevels = calculateProgressiveTax(taxableIncome);

    // Tổng thuế từ các bậc
    var taxAmount = progressiveTaxLevels.values().stream().mapToDouble(Double::doubleValue).sum();

    return PersonalTaxDTO.builder()
        .pretaxSalary(pretaxSalary)
        .deductedAmount(dependantDeduction)
        .taxableIncome(taxableIncome)
        .taxAmount(taxAmount)
        .progressiveTaxLevels(progressiveTaxLevels)
        .netIncome(totalSalary - taxAmount - totalInsurance)
        .build();
  }

  static Map<String, Double> calculateProgressiveTax(double taxableIncome) {
    var resultBuilder = ImmutableMap.<String, Double>builder();

    var taxLevelOrdinal = 0;

    while (true) {
      var taxLevel = TaxLevel.parseTaxLevel(taxLevelOrdinal);

      var deltaToNextLevel = taxableIncome - taxLevel.threshold();

      if (deltaToNextLevel < 0) {
        break;
      }

      var nextTaxLevel = TaxLevel.parseTaxLevel(taxLevelOrdinal + 1);

      // TN chịu thuế lớn hơn bậc tiếp -> (bậc tiếp - bậc hiện tại) * mức thuế bậc tiếp
      // TN chịu thuế nhỏ hơn bậc tiếp -> (TN chịu thuế - bậc hiện tại) * mức thuế bậc tiếp
      var delta =
          taxableIncome < nextTaxLevel.threshold()
              ? deltaToNextLevel
              : nextTaxLevel.threshold() - taxLevel.threshold();

      if (delta > 0) {
        resultBuilder.put(taxLevel.name(), nextTaxLevel.rate() * delta);
      }

      taxLevelOrdinal++;
    }

    return resultBuilder.build();
  }

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
  enum TaxLevel {
    LEVEL_0(0.0, 0.0),
    LEVEL_1(5_000_000.0, 0.05),
    LEVEL_2(10_000_000.0, 0.10),
    LEVEL_3(18_000_000.0, 0.15),
    LEVEL_4(32_000_000.0, 0.2),
    LEVEL_5(52_000_000.0, 0.25),
    LEVEL_6(80_000_000.0, 0.3),
    LEVEL_7(Double.MAX_VALUE, 0.35); // Your income cannot be more than total assets of the world!!!

    final double threshold;
    final double rate;

    static final Map<Integer, TaxLevel> MAPS =
        Arrays.stream(values()).collect(Collectors.toMap(TaxLevel::ordinal, Function.identity()));

    public static TaxLevel parseTaxLevel(int taxLevel) {
      return Optional.ofNullable(MAPS.get(taxLevel))
          .orElseThrow(() -> new IllegalArgumentException("taxLevel = %d".formatted(taxLevel)));
    }
  }

}
