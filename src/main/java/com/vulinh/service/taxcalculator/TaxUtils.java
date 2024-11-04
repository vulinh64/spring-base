package com.vulinh.service.taxcalculator;

import com.google.common.collect.ImmutableMap;
import com.vulinh.service.taxcalculator.TaxDetailDTO.InsuranceDTO;
import com.vulinh.service.taxcalculator.TaxDetailDTO.PersonalTaxDTO;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TaxUtils {

  public static InsuranceDTO calculateInsurance(TaxDetailDTO taxDetailDTO) {
    var basicSalary = taxDetailDTO.basicSalary();

    // Được đóng bảo hiểm khi tổng lương >= Lương đóng bảo hiểm
    var isEligibleForInsurance = taxDetailDTO.totalSalary() >= basicSalary;

    var healthInsurance =
        isEligibleForInsurance ? basicSalary * InsuranceRate.HEALTH_INSURANCE.rate() : 0;

    var socialInsurance =
        isEligibleForInsurance ? basicSalary * InsuranceRate.SOCIAL_INSURANCE.rate() : 0;

    var unemploymentInsurance =
        isEligibleForInsurance ? basicSalary * InsuranceRate.UNEMPLOYMENT_INSURANCE.rate() : 0;

    return InsuranceDTO.builder()
        .healthInsurance(healthInsurance)
        .socialInsurance(socialInsurance)
        .unemploymentInsurance(unemploymentInsurance)
        .totalInsurance(healthInsurance + socialInsurance + unemploymentInsurance)
        .build();
  }

  public static PersonalTaxDTO calculatePersonalTax(
      TaxDetailDTO taxDetailDTO, InsuranceDTO insuranceDTO) {
    var totalSalary = taxDetailDTO.totalSalary();

    // Tổng đóng BH
    var totalInsurance = insuranceDTO.totalInsurance();

    // Thu nhập trước thuế = Tổng thu nhập - Bảo hiểm
    var pretaxSalary = totalSalary - totalInsurance;

    // Người phụ thuộc * Giảm trừ mỗi người phụ thuộc
    var deductedAmount =
        TaxConstant.DEDUCTION_PER_DEPENDANTS.value() * taxDetailDTO.numberOfDependants();

    // Thu nhập chịu thuế = Thu nhập trước thuế - Thu nhập miễn thuế - Giảm trừ người phụ thuộc
    var taxableIncome = pretaxSalary - (TaxConstant.NON_TAXABLE_INCOME.value() + deductedAmount);

    // Thu nhập chịu thuế luôn phải lớn hơn hoặc bằng 0
    if (taxableIncome < 0) {
      taxableIncome = 0.0;
    }

    // Thuế lũy tiến
    var progressiveTax = calculateProgressiveTax(taxableIncome);

    // Tổng thuế từ các bậc
    var taxAmount = progressiveTax.values().stream().mapToDouble(Double::doubleValue).sum();

    return PersonalTaxDTO.builder()
        .pretaxSalary(pretaxSalary)
        .deductedAmount(deductedAmount)
        .taxableIncome(taxableIncome)
        .taxAmount(taxAmount)
        .progressiveTax(progressiveTax)
        .netIncome(totalSalary - taxAmount - totalInsurance)
        .build();
  }

  private static Map<String, Double> calculateProgressiveTax(double taxableIncome) {
    // Tính bậc thuế
    var taxLevel = calculateTaxLevel(taxableIncome);

    if (taxLevel == TaxLevel.LEVEL_0) {
      return Collections.emptyMap();
    }

    var currentOrdinal = taxLevel.ordinal();

    var resultBuilder = ImmutableMap.<String, Double>builder();

    // Tiền thuế các bậc thuế trước
    for (var previousLevel = 1; previousLevel < currentOrdinal; previousLevel++) {
      var currentTaxLevel = TaxLevel.parseTaxLevel(previousLevel);
      var previousTaxLevel = TaxLevel.parseTaxLevel(previousLevel - 1);

      resultBuilder.put(
          currentTaxLevel.name(),
          (currentTaxLevel.threshold() - previousTaxLevel.threshold()) * currentTaxLevel.rate());
    }

    // Tiền thuế bậc hiện tại = (thu nhập chịu thuế - thu nhập ngưỡng bậc trước) * mức thuế (%)
    return resultBuilder
        .put(
            taxLevel.name(),
            (taxableIncome - TaxLevel.parseTaxLevel(currentOrdinal - 1).threshold())
                * taxLevel.rate())
        .build();
  }

  private static TaxLevel calculateTaxLevel(double taxableIncome) {
    var taxLevel = TaxLevel.LEVEL_0;

    while (taxableIncome > taxLevel.threshold()) {
      taxLevel = TaxLevel.parseTaxLevel(taxLevel.ordinal() + 1);
    }

    return taxLevel;
  }

  @RequiredArgsConstructor
  @Getter
  @Accessors(fluent = true)
  private enum InsuranceRate {
    SOCIAL_INSURANCE(0.08),
    HEALTH_INSURANCE(0.015),
    UNEMPLOYMENT_INSURANCE(0.01);

    private final double rate;
  }

  @RequiredArgsConstructor
  @Getter
  @Accessors(fluent = true)
  private enum TaxConstant {
    NON_TAXABLE_INCOME(11_000_000),
    DEDUCTION_PER_DEPENDANTS(4_400_000);

    private final double value;
  }

  @RequiredArgsConstructor
  @Getter
  @Accessors(fluent = true)
  private enum TaxLevel {
    LEVEL_0(0.0, 0.0),
    LEVEL_1(5_000_000.0, 0.05),
    LEVEL_2(10_000_000.0, 0.10),
    LEVEL_3(18_000_000.0, 0.15),
    LEVEL_4(32_000_000.0, 0.2),
    LEVEL_5(52_000_000.0, 0.25),
    LEVEL_6(80_000_000.0, 0.3),
    LEVEL_7(Double.MAX_VALUE, 0.35); // Your income cannot be more than total assets of the world!!!

    private final double threshold;
    private final double rate;

    private static final Map<Integer, TaxLevel> MAPS =
        Arrays.stream(values()).collect(Collectors.toMap(TaxLevel::ordinal, Function.identity()));

    public static TaxLevel parseTaxLevel(int taxLevel) {
      return Optional.ofNullable(MAPS.get(taxLevel))
          .orElseThrow(() -> new IllegalArgumentException("taxLevel = %d".formatted(taxLevel)));
    }
  }
}
