package com.vulinh.service.taxcalculator;

import com.vulinh.data.dto.message.I18NCapable;
import com.vulinh.exception.CommonException;
import com.vulinh.service.taxcalculator.TaxUtils.TaxConstant;
import java.io.Serial;
import java.util.Map;
import lombok.Builder;
import lombok.With;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;

@With
@Builder
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
      Map<String, Double> progressiveTaxLevels,
      double netIncome) {}

  static class TaxCalculatorException extends CommonException {
    @Serial private static final long serialVersionUID = 3965734369760897819L;

    public TaxCalculatorException(String message, String singletonMessage) {
      super(
          message,
          new I18NCapable() {

            @Serial private static final long serialVersionUID = -5502306451911678107L;

            @Override
            @NonNull
            public HttpStatusCode getHttpStatusCode() {
              return HttpStatus.BAD_REQUEST;
            }

            @Override
            @NonNull
            public String getErrorCode() {
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
