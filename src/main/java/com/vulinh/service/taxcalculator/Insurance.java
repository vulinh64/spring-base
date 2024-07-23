package com.vulinh.service.taxcalculator;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record Insurance(
    @JsonProperty("BHXH") BigDecimal socialInsurance,
    @JsonProperty("BHYT") BigDecimal healthInsurance,
    @JsonProperty("BH thất nghiệp") BigDecimal unemploymentInsurance,
    @JsonProperty("Tổng đóng BH") BigDecimal totalInsurance) {}
