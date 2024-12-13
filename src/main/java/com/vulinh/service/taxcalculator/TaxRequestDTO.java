package com.vulinh.service.taxcalculator;

import com.vulinh.data.dto.message.WithHttpStatusCode;
import com.vulinh.exception.CommonException;
import com.vulinh.service.taxcalculator.TaxUtils.TaxConstant;
import java.io.Serial;
import java.util.Map;
import lombok.Builder;
import lombok.With;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;

@Builder
@With
public record TaxRequestDTO(double totalSalary, double basicSalary, int numberOfDependants) {

  static final String PLACEHOLDER_ERROR_CODE = "OTHER";

  public TaxRequestDTO {
    var maxBasicSalary = TaxConstant.MAX_BASIC_SALARY.value();
    var minBasicSalary = TaxConstant.MIN_BASIC_SALARY.value();

    basicSalary = Math.min(basicSalary, maxBasicSalary);

    if (basicSalary < minBasicSalary) {
      throw new TaxCalculatorException(
          "Basic salary (%s) cannot be less than %s"
              .formatted(
                  TaxMapper.toBigDecimal(basicSalary), TaxMapper.toBigDecimal(minBasicSalary)),
          "Basic salary cannot be less than %s".formatted(minBasicSalary));
    }

    if (basicSalary > totalSalary) {
      throw new TaxCalculatorException(
          "Basic salary (%s) cannot exceed total salary (%s)"
              .formatted(TaxMapper.toBigDecimal(basicSalary), TaxMapper.toBigDecimal(totalSalary)),
          "Basic salary cannot exceed total salary");
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
      Map<String, Double> progressiveTax,
      double netIncome) {}

  static class TaxCalculatorException extends CommonException {
    public TaxCalculatorException(String message, String singletonMessage) {
      super(
          message,
          new WithHttpStatusCode() {

            @Serial private static final long serialVersionUID = 5869679594561484928L;

            @Override
            public HttpStatusCode getHttpStatusCode() {
              return HttpStatus.BAD_REQUEST;
            }

            @Override
            @NonNull
            public String getCode() {
              return PLACEHOLDER_ERROR_CODE;
            }

            @Override
            public String getDisplayMessage(Object... ignored) {
              return singletonMessage;
            }
          },
          null);
    }
  }
}
