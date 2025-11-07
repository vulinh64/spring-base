package com.vulinh.service.taxcalculator;

import com.vulinh.service.taxcalculator.TaxSupport.TaxPeriod;
import java.time.LocalDate;
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

    return calculateNonProbation(
        taxRequest, LocalDate.now().getYear() >= 2026 ? TaxPeriod.POST_2026 : TaxPeriod.PRE_2026);
  }

  static TaxResponse calculateProbation(TaxRequest taxRequest) {
    // Thuế TNCN thử việc
    var personalTax = TaxUtils.calculatePersonalTaxProbation(taxRequest);

    return TAX_MAPPER.toTaxDetail(taxRequest, null, personalTax);
  }

  static TaxResponse calculateNonProbation(TaxRequest taxRequest, TaxPeriod taxPeriod) {
    // Bảo hiểm - BHYT (1.5%), BHXH (8%), BH thất nghiệp (1%)
    var insurance = TaxUtils.calculateInsurance(taxRequest);

    // Thuế TNCN
    var personalTax = TaxUtils.calculatePersonalTax(taxRequest, insurance, taxPeriod);

    return TAX_MAPPER.toTaxDetail(taxRequest, insurance, personalTax);
  }
}
