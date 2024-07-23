package com.vulinh.service.taxcalculator;

import java.util.Map;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record PersonalTaxDTO(
    double pretaxSalary,
    double taxableIncome,
    double deductedAmount,
    double taxAmount,
    Map<String, Double> progressiveTax,
    double netIncome) {}
