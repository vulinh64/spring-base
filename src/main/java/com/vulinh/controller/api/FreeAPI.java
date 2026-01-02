package com.vulinh.controller.api;

import com.vulinh.data.constant.EndpointConstant;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RequestMapping(EndpointConstant.ENDPOINT_FREE)
@Tag(name = "Free API controller", description = "Public test API")
public interface FreeAPI {

  @GetMapping("/tax-calculator")
  Object calculateTax(
      @RequestParam Double totalSalary,
      @RequestParam Double basicSalary,
      @RequestParam(defaultValue = "0") Integer numberOfDependants,
      @RequestParam(defaultValue = "false") Boolean isProbation,
      @RequestParam(defaultValue = "0.85") Double probationPercentage);
}
