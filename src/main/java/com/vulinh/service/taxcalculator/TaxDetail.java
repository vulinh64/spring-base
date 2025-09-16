package com.vulinh.service.taxcalculator;

import module java.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record TaxDetail(
    @JsonProperty("Tổng thu nhập") BigDecimal totalSalary,
    @JsonProperty("Lương cơ bản") BigDecimal basicSalary,
    @JsonProperty("Số người phụ thuộc") int numberOfDependants,
    @JsonProperty("Bảo hiểm") Insurance insurance,
    @JsonProperty("Thuế TNCN") PersonalTax personalTax,
    @JsonProperty("Thử việc") boolean isProbation,
    @JsonProperty("Mức thử việc") BigDecimal probationPercentage) {

  @Builder
  @With
  record Insurance(
      @JsonProperty("BHXH") BigDecimal socialInsurance,
      @JsonProperty("BHYT") BigDecimal healthInsurance,
      @JsonProperty("BH thất nghiệp") BigDecimal unemploymentInsurance,
      @JsonProperty("Tổng đóng BH") BigDecimal totalInsurance) {}

  @Builder
  @With
  record PersonalTax(
      @JsonProperty("Thu nhập trước thuế") BigDecimal pretaxSalary,
      @JsonProperty("Thu nhập chịu thuế") BigDecimal taxableIncome,
      @JsonProperty("Giảm trừ người phụ thuộc") BigDecimal deductedAmount,
      @JsonProperty("Thuế") BigDecimal taxAmount,
      @JsonProperty("Thuế lũy tiến") Map<String, BigDecimal> progressiveTaxLevels,
      @JsonProperty("Thu nhập sau thuế") BigDecimal netIncome) {}
}
