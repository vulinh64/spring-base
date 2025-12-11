package com.vulinh.service.taxcalculator;

import module java.base;

import com.google.common.collect.ImmutableList;
import com.vulinh.service.taxcalculator.TaxSupport.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TaxUtils {

  static PersonalTaxDTO calculatePersonalTaxProbation(TaxRequest taxRequest) {
    var totalSalary = taxRequest.totalSalary();
    var taxableAMount = totalSalary * taxRequest.probationPercentage();
    var taxAmount = taxableAMount * ProbationRate.DEDUCTION_PERCENTAGE.percentage();

    return PersonalTaxDTO.builder()
        .pretaxSalary(totalSalary)
        .taxAmount(taxAmount)
        .taxableIncome(taxableAMount)
        .netIncome(taxableAMount - taxAmount)
        .build();
  }

  static InsuranceDTO calculateInsurance(TaxRequest taxRequest) {
    var basicSalary = taxRequest.basicSalary();

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

  static PersonalTaxDTO calculatePersonalTax(
      TaxRequest taxRequest,
      InsuranceDTO insuranceDTO,
      TaxDeductionPeriod taxDeductionPeriod,
      ProgressiveTaxPeriod progressiveTaxPeriod) {
    var totalSalary = taxRequest.totalSalary();

    // Tổng đóng BH
    var totalInsurance = insuranceDTO.totalInsurance();

    // Thu nhập trước thuế = Tổng thu nhập - Bảo hiểm
    var pretaxSalary = totalSalary - totalInsurance;

    // Người phụ thuộc * Giảm trừ mỗi người phụ thuộc
    var dependantDeduction =
        taxDeductionPeriod.dependantDeduction() * taxRequest.numberOfDependants();

    // Thu nhập chịu thuế = Thu nhập trước thuế - Thu nhập miễn thuế - Giảm trừ người phụ thuộc
    var taxableIncome =
        pretaxSalary - (taxDeductionPeriod.personalDeduction() + dependantDeduction);

    // Thu nhập chịu thuế luôn phải lớn hơn hoặc bằng 0
    if (taxableIncome < 0) {
      taxableIncome = 0.0;
    }

    // Thuế lũy tiến
    var progressiveTaxLevels = calculateProgressiveTax(taxableIncome, progressiveTaxPeriod);

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

  static List<Double> calculateProgressiveTax(
      double taxableIncome, ProgressiveTaxPeriod taxDeductionPeriod) {
    var resultBuilder = ImmutableList.<Double>builder();

    var taxLevelOrdinal = 0;

    var progressiveTaxLevel = taxDeductionPeriod.levels();

    while (true) {
      var taxLevel = progressiveTaxLevel.get(taxLevelOrdinal);

      var currentThreshold = taxLevel.threshold();

      var deltaToNextLevel = taxableIncome - currentThreshold;

      if (deltaToNextLevel < 0) {
        break;
      }

      var nextTaxLevel = progressiveTaxLevel.get(taxLevelOrdinal + 1);

      // TN chịu thuế lớn hơn bậc tiếp -> (bậc tiếp - bậc hiện tại) * mức thuế bậc tiếp
      // TN chịu thuế nhỏ hơn bậc tiếp -> (TN chịu thuế - bậc hiện tại) * mức thuế bậc tiếp
      var nextThreshold = nextTaxLevel.threshold();

      var delta =
          taxableIncome < nextThreshold ? deltaToNextLevel : nextThreshold - currentThreshold;

      if (delta > 0) {
        resultBuilder.add(nextTaxLevel.rate() * delta);
      }

      taxLevelOrdinal++;
    }

    return resultBuilder.build();
  }
}
