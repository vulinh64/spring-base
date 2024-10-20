package com.vulinh.controller.impl;

import com.vulinh.controller.api.FreeAPI;
import com.vulinh.factory.GenericResponseFactory;
import com.vulinh.service.taxcalculator.TaxDetailDTO;
import com.vulinh.service.taxcalculator.TaxService;
import com.vulinh.utils.springcron.dto.SpringCronGeneratorDTO;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FreeController implements FreeAPI {

  private final TaxService taxService;

  @Override
  public Object calculateTax(Double totalSalary, Double basicSalary, Integer numberOfDependants) {
    return GenericResponseFactory.INSTANCE.success(
        taxService.calculate(
            TaxDetailDTO.builder()
                .totalSalary(totalSalary)
                .basicSalary(basicSalary)
                .numberOfDependants(numberOfDependants)
                .build()));
  }

  @Override
  public Object generateSpringCronExpression(
      @NonNull SpringCronGeneratorDTO springCronGeneratorDTO) {
    return Map.of("expression", springCronGeneratorDTO.toCronExpression());
  }
}
