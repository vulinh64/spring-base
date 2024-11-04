package com.vulinh.service.taxcalculator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaxService {

  public TaxDetail calculate(TaxDetailDTO taxDetailDTO) {
    log.debug(
        "Inside calculating personal tax according to Law of Vietnam... Looking at thread: {}",
        Thread.currentThread());

    // Bảo hiểm - BHYT (1.5%), BHXH (8%), BH thất nghiệp (1%)
    var insurance = TaxUtils.calculateInsurance(taxDetailDTO);

    // Thuế TNCN
    var personalTax = TaxUtils.calculatePersonalTax(taxDetailDTO, insurance);

    return TaxMapper.INSTANCE.toTaxDetail(
        taxDetailDTO.withPersonalTax(personalTax).withInsurance(insurance));
  }
}
