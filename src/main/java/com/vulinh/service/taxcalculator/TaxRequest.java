package com.vulinh.service.taxcalculator;

import module java.base;

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

    if (basicSalary < minBasicSalary) {
      throw new TaxCalculatorException(
          "Basic salary (%s) cannot be less than %s"
              .formatted(
                  TAX_MAPPER.toBigDecimal(basicSalary), TAX_MAPPER.toBigDecimal(minBasicSalary)));
    }

    if (basicSalary > totalSalary) {
      throw new TaxCalculatorException(
          "Basic salary (%s) cannot exceed total salary (%s)"
              .formatted(
                  TAX_MAPPER.toBigDecimal(basicSalary), TAX_MAPPER.toBigDecimal(totalSalary)));
    }
  }
}
