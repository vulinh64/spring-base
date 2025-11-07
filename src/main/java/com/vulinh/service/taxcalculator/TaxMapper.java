package com.vulinh.service.taxcalculator;

import module java.base;

import com.vulinh.service.taxcalculator.TaxSupport.InsuranceDTO;
import com.vulinh.service.taxcalculator.TaxSupport.PersonalTaxDTO;
import com.vulinh.service.taxcalculator.TaxResponse.Insurance;
import com.vulinh.service.taxcalculator.TaxResponse.PersonalTax;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
interface TaxMapper {

  TaxMapper INSTANCE = Mappers.getMapper(TaxMapper.class);

  default BigDecimal toBigDecimal(double value) {
    return BigDecimal.valueOf(value).setScale(2, RoundingMode.CEILING);
  }

  default List<BigDecimal> toBigDecimalList(List<Double> values) {
    return values == null
        ? Collections.emptyList()
        : values.stream().map(this::toBigDecimal).collect(Collectors.toList());
  }

  Insurance toInsurance(InsuranceDTO insuranceDTO);

  PersonalTax toPersonalTax(PersonalTaxDTO personalTaxDTO);

  TaxResponse toTaxDetail(
      TaxRequest taxRequest, InsuranceDTO insurance, PersonalTaxDTO personalTax);
}
