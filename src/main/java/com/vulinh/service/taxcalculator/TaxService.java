package com.vulinh.service.taxcalculator;

import module java.base;

import com.vulinh.service.taxcalculator.TaxSupport.ProgressiveTaxPeriod;
import com.vulinh.service.taxcalculator.TaxSupport.TaxDeductionPeriod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaxService {

  static final TaxMapper TAX_MAPPER = TaxMapper.INSTANCE;

  public TaxResponse calculate(TaxRequest taxRequest) {
    log.debug(
        "Inside calculating personal tax according to Law of Vietnam... Looking at thread: {}",
        Thread.currentThread());

    if (taxRequest.isProbation()) {
      return calculateProbation(taxRequest);
    }

    var today = LocalDate.now();

    return calculateNonProbation(
        taxRequest,
        TaxDeductionPeriod.fromYear(today.getYear()),
        ProgressiveTaxPeriod.fromDate(today));
  }

  static TaxResponse calculateProbation(TaxRequest taxRequest) {
    // Thuế TNCN thử việc
    var personalTax = TaxUtils.calculatePersonalTaxProbation(taxRequest);

    return TAX_MAPPER.toTaxDetail(taxRequest, null, personalTax);
  }

  static TaxResponse calculateNonProbation(
      TaxRequest taxRequest,
      TaxDeductionPeriod taxDeductionPeriod,
      ProgressiveTaxPeriod progressiveTaxPeriod) {
    // Bảo hiểm - BHYT (1.5%), BHXH (8%), BH thất nghiệp (1%)
    var insurance = TaxUtils.calculateInsurance(taxRequest);

    // Thuế TNCN
    var personalTax =
        TaxUtils.calculatePersonalTax(
            taxRequest, insurance, taxDeductionPeriod, progressiveTaxPeriod);

    return TAX_MAPPER.toTaxDetail(taxRequest, insurance, personalTax);
  }
}
