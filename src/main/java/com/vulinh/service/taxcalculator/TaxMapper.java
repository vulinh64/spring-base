package com.vulinh.service.taxcalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
interface TaxMapper {

  TaxMapper INSTANCE = Mappers.getMapper(TaxMapper.class);

  static BigDecimal toBigDecimal(double value) {
    return BigDecimal.valueOf(value).setScale(2, RoundingMode.CEILING);
  }

  static Map<String, BigDecimal> toBigDecimalMap(Map<String, Double> values) {
    return values.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> toBigDecimal(entry.getValue())));
  }

  TaxDetail.Insurance toInsurance(TaxDetailDTO.InsuranceDTO insuranceDTO);

  TaxDetail.PersonalTax toPersonalTax(TaxDetailDTO.PersonalTaxDTO personalTaxDTO);

  TaxDetail toTaxDetail(TaxDetailDTO taxDetailDTO);
}
