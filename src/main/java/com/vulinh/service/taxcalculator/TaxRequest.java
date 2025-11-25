package com.vulinh.service.taxcalculator;

import module java.base;

import com.vulinh.service.taxcalculator.TaxSupport.ProbationRate;
import com.vulinh.service.taxcalculator.TaxSupport.TaxCalculatorException;
import com.vulinh.service.taxcalculator.TaxSupport.TaxConstant;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record TaxRequest(
    double totalSalary,
    double basicSalary,
    int numberOfDependants,
    boolean isProbation,
    double probationPercentage) {

  static final TaxMapper TAX_MAPPER = TaxMapper.INSTANCE;

  public TaxRequest {
    // Auto set basic salary to maximum if it exceeds the limit
    basicSalary = Math.min(basicSalary, TaxConstant.MAX_BASIC_SALARY.value());

    var minBasicSalary = TaxConstant.MIN_BASIC_SALARY.value();

    // Basic salary must not be less than minimum basic salary
    if (basicSalary < minBasicSalary) {
      throw new TaxCalculatorException(
          "Basic salary (%s) cannot be less than %s"
              .formatted(
                  TAX_MAPPER.toBigDecimal(basicSalary), TAX_MAPPER.toBigDecimal(minBasicSalary)));
    }

    // Basic salary must not exceed total salary
    if (basicSalary > totalSalary) {
      throw new TaxCalculatorException(
          "Basic salary (%s) cannot exceed total salary (%s)"
              .formatted(
                  TAX_MAPPER.toBigDecimal(basicSalary), TAX_MAPPER.toBigDecimal(totalSalary)));
    }

    // Probation percentage must be between 85% and 100%
    if (isProbation
        && (Double.compare(probationPercentage, ProbationRate.MIN_PERCENTAGE.percentage()) < 0
            || Double.compare(probationPercentage, ProbationRate.MAX_PERCENTAGE.percentage()) > 0)) {
      throw new TaxCalculatorException("Invalid probation percentage");
    }
  }
}
