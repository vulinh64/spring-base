package com.vulinh.controller.impl;

import module java.base;

import com.vulinh.controller.api.FreeAPI;
import com.vulinh.data.dto.response.GenericResponse.ResponseCreator;
import com.vulinh.service.taxcalculator.TaxRequest;
import com.vulinh.service.taxcalculator.TaxService;
import com.vulinh.utils.springcron.dto.SpringCronGeneratorDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FreeController implements FreeAPI {

  final TaxService taxService;

  @Override
  public Object calculateTax(
      Double totalSalary,
      Double basicSalary,
      Integer numberOfDependants,
      Boolean isProbation,
      Double probationPercentage) {
    return ResponseCreator.success(
        taxService.calculate(
            TaxRequest.builder()
                .totalSalary(totalSalary)
                .basicSalary(basicSalary)
                .numberOfDependants(numberOfDependants)
                .isProbation(isProbation)
                .probationPercentage(probationPercentage)
                .build()));
  }

  @Override
  public Object generateSpringCronExpression(
      @NonNull SpringCronGeneratorDTO springCronGeneratorDTO) {
    return Map.of("expression", springCronGeneratorDTO.toCronExpression());
  }
}
