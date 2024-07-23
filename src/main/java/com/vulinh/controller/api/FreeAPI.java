package com.vulinh.controller.api;

import com.vulinh.constant.EndpointConstant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(EndpointConstant.ENDPOINT_FREE)
public interface FreeAPI {

  @GetMapping("/tax-calculator")
  Object calculateTax(
      @RequestParam Double totalSalary,
      @RequestParam Double basicSalary,
      @RequestParam(defaultValue = "0") Integer numberOfDependants);
}
