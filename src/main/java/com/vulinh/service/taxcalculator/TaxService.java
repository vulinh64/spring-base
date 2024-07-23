package com.vulinh.service.taxcalculator;

import org.springframework.stereotype.Service;

@Service
public class TaxService {

  private static final TaxMapper TAX_MAPPER = TaxMapper.INSTANCE;

  public TaxDetail calculate(TaxDetailDTO taxDetailDTO) {
    // Bảo hiểm - BHYT (1.5%), BHXH (8%), BH thất nghiệp (1%)
    var insurance = TaxUtils.calculateInsurance(taxDetailDTO);

    // Thuế TNCN
    var personalTax = TaxUtils.calculatePersonalTax(taxDetailDTO, insurance);

    return TAX_MAPPER.toTaxDetail(
        taxDetailDTO.withPersonalTax(personalTax).withInsurance(insurance));
  }
}
