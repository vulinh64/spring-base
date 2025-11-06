package com.vulinh.service.taxcalculator;

import module java.base;

import com.google.common.collect.ImmutableList;
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
    var dependantDeduction = taxPeriod.dependantDeduction() * taxRequestDTO.numberOfDependants();

    // Thu nhập chịu thuế = Thu nhập trước thuế - Thu nhập miễn thuế - Giảm trừ người phụ thuộc
    var taxableIncome = pretaxSalary - (taxPeriod.personalDeduction() + dependantDeduction);

    // Thu nhập chịu thuế luôn phải lớn hơn hoặc bằng 0
    if (taxableIncome < 0) {
      taxableIncome = 0.0;
    }

    // Thuế lũy tiến
    var progressiveTaxLevels = calculateProgressiveTax(taxableIncome, taxPeriod);

    // Tổng thuế từ các bậc
    var taxAmount = progressiveTaxLevels.stream().mapToDouble(Double::doubleValue).sum();

    return PersonalTaxDTO.builder()
        .pretaxSalary(pretaxSalary)
        .deductedAmount(dependantDeduction)
        .taxableIncome(taxableIncome)
        .taxAmount(taxAmount)
        .progressiveTaxLevels(progressiveTaxLevels)
        .netIncome(totalSalary - taxAmount - totalInsurance)
        .build();
  }

  static List<Double> calculateProgressiveTax(double taxableIncome, TaxPeriod taxPeriod) {
    var resultBuilder = ImmutableList.<Double>builder();

    var taxLevelOrdinal = 0;

    List<TaxPeriod.ProgressiveTaxLevel> progressiveTaxLevels = taxPeriod.progressiveTaxLevel();

    while (true) {
      var taxLevel = progressiveTaxLevels.get(taxLevelOrdinal);

      var deltaToNextLevel = taxableIncome - taxLevel.threshold();

      if (deltaToNextLevel < 0) {
        break;
      }

      var nextTaxLevel = progressiveTaxLevels.get(taxLevelOrdinal + 1);

      // TN chịu thuế lớn hơn bậc tiếp -> (bậc tiếp - bậc hiện tại) * mức thuế bậc tiếp
      // TN chịu thuế nhỏ hơn bậc tiếp -> (TN chịu thuế - bậc hiện tại) * mức thuế bậc tiếp
      var delta =
          taxableIncome < nextTaxLevel.threshold()
              ? deltaToNextLevel
              : nextTaxLevel.threshold() - taxLevel.threshold();

      if (delta > 0) {
        resultBuilder.add(nextTaxLevel.rate() * delta);
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
}
