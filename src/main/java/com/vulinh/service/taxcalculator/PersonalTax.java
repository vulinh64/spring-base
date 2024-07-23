package com.vulinh.service.taxcalculator;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Map;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record PersonalTax(
    @JsonProperty("Thu nhập trước thuế") BigDecimal pretaxSalary,
    @JsonProperty("Thu nhập chịu thuế") BigDecimal taxableIncome,
    @JsonProperty("Giảm trừ người phụ thuộc") BigDecimal deductedAmount,
    @JsonProperty("Thuế") BigDecimal taxAmount,
    @JsonProperty("Thuế lũy tiến") Map<String, BigDecimal> progressiveTax,
    @JsonProperty("Thu nhập sau thuế") BigDecimal netIncome) {}
