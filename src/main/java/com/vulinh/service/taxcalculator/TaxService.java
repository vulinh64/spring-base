package com.vulinh.service.taxcalculator;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaxService {

  public TaxDetail calculate(TaxRequestDTO taxRequestDTO) {
    log.debug(
        "Inside calculating personal tax according to Law of Vietnam... Looking at thread: {}",
        Thread.currentThread());

    if (taxRequestDTO.isProbation()) {
      return calculateProbation(taxRequestDTO);
    }

    return calculateNonProbation(
        taxRequestDTO,
        LocalDate.now().getYear() == 2026 ? TaxPeriod.POST_2026 : TaxPeriod.PRE_2026);
  }

  public static TaxDetail calculateProbation(TaxRequestDTO taxRequestDTO) {
    // Thuế TNCN thử việc
    var personalTax = TaxUtils.calculatePersonalTaxProbation(taxRequestDTO);

    return TaxMapper.INSTANCE.toTaxDetail(taxRequestDTO, null, personalTax);
  }

  static TaxDetail calculateNonProbation(TaxRequestDTO taxRequestDTO, TaxPeriod taxPeriod) {
    // Bảo hiểm - BHYT (1.5%), BHXH (8%), BH thất nghiệp (1%)
    var insurance = TaxUtils.calculateInsurance(taxRequestDTO);

    // Thuế TNCN
    var personalTax = TaxUtils.calculatePersonalTax(taxRequestDTO, insurance, taxPeriod);

    return TaxMapper.INSTANCE.toTaxDetail(taxRequestDTO, insurance, personalTax);
  }
}
