package com.vulinh.service.taxcalculator;

import java.util.Map;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record TaxDetailDTO(
    double totalSalary,
    double basicSalary,
    int numberOfDependants,
    InsuranceDTO insurance,
    PersonalTaxDTO personalTax) {

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
      Map<String, Double> progressiveTax,
      double netIncome) {}
}
