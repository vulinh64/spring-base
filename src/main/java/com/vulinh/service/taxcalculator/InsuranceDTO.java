package com.vulinh.service.taxcalculator;

import lombok.Builder;
import lombok.With;

@Builder
@With
public record InsuranceDTO(
    double socialInsurance,
    double healthInsurance,
    double unemploymentInsurance,
    double totalInsurance) {}
