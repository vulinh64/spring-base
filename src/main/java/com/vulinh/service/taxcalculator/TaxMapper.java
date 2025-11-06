package com.vulinh.service.taxcalculator;

import module java.base;

import com.vulinh.service.taxcalculator.TaxDetail.Insurance;
import com.vulinh.service.taxcalculator.TaxDetail.PersonalTax;
import com.vulinh.service.taxcalculator.TaxRequestDTO.InsuranceDTO;
import com.vulinh.service.taxcalculator.TaxRequestDTO.PersonalTaxDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
interface TaxMapper {

  TaxMapper INSTANCE = Mappers.getMapper(TaxMapper.class);

  static BigDecimal toBigDecimal(double value) {
    return BigDecimal.valueOf(value).setScale(2, RoundingMode.CEILING);
  }

  static List<BigDecimal> toBigDecimalList(List<Double> values) {
    return values.stream().map(TaxMapper::toBigDecimal).collect(Collectors.toList());
  }

  Insurance toInsurance(InsuranceDTO insuranceDTO);

  PersonalTax toPersonalTax(PersonalTaxDTO personalTaxDTO);

  TaxDetail toTaxDetail(
      TaxRequestDTO taxRequestDTO, InsuranceDTO insurance, PersonalTaxDTO personalTax);
}
