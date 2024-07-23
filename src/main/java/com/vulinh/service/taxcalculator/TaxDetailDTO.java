package com.vulinh.service.taxcalculator;

import lombok.Builder;
import lombok.With;

@Builder
@With
public record TaxDetailDTO(
    double totalSalary,
    double basicSalary,
    int numberOfDependants,
    InsuranceDTO insurance,
    PersonalTaxDTO personalTax) {}
