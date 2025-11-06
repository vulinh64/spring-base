package com.vulinh.service.taxcalculator;

import module java.base;

import com.vulinh.exception.ApplicationException;
import com.vulinh.service.taxcalculator.TaxUtils.TaxConstant;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record TaxRequestDTO(
    double totalSalary,
    double basicSalary,
    int numberOfDependants,
    boolean isProbation,
    double probationPercentage) {

  static final String PLACEHOLDER_ERROR_CODE = "OTHER";

  public TaxRequestDTO {
    var maxBasicSalary = TaxConstant.MAX_BASIC_SALARY.value();
    var minBasicSalary = TaxConstant.MIN_BASIC_SALARY.value();

    basicSalary = Math.min(basicSalary, maxBasicSalary);

    if (basicSalary < minBasicSalary) {
      throw new TaxCalculatorException(
          "Basic salary (%s) cannot be less than %s"
              .formatted(
                  TaxMapper.toBigDecimal(basicSalary), TaxMapper.toBigDecimal(minBasicSalary)));
    }

    if (basicSalary > totalSalary) {
      throw new TaxCalculatorException(
          "Basic salary (%s) cannot exceed total salary (%s)"
              .formatted(TaxMapper.toBigDecimal(basicSalary), TaxMapper.toBigDecimal(totalSalary)));
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

  static class TaxCalculatorException extends ApplicationException {

    @Serial private static final long serialVersionUID = -4699666572519989228L;

    protected TaxCalculatorException(String message) {
      super(message, () -> "MXXXX", null);
    }
  }
}
