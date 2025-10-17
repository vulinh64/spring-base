package com.vulinh.service.taxcalculator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TaxServiceTest {

  final TaxService taxService = new TaxService();

  // Lương thực nhận: 34M
  // Lương đóng BH: 6.5M
  // Có 1 người phụ thuộc
  @Test
  void calculateTax01() {
    var result =
        taxService.calculate(
            TaxRequestDTO.builder()
                .totalSalary(34_000_000L)
                .basicSalary(6_500_000L)
                .numberOfDependants(1)
                .build());

    var socialCoverage = result.insurance();

    assertEquals(TaxMapper.toBigDecimal(520_000), socialCoverage.socialInsurance());
    assertEquals(TaxMapper.toBigDecimal(97_500), socialCoverage.healthInsurance());
    assertEquals(TaxMapper.toBigDecimal(65_000), socialCoverage.unemploymentInsurance());
    assertEquals(TaxMapper.toBigDecimal(682_500), socialCoverage.totalInsurance());

    var personalTax = result.personalTax();

    assertEquals(TaxMapper.toBigDecimal(33_317_500), personalTax.pretaxSalary());
    assertEquals(TaxMapper.toBigDecimal(17_917_500), personalTax.taxableIncome());
    assertEquals(TaxMapper.toBigDecimal(1_937_625), personalTax.taxAmount());
    assertEquals(TaxMapper.toBigDecimal(4_400_000), personalTax.deductedAmount());
    assertEquals(TaxMapper.toBigDecimal(31_379_875), personalTax.netIncome());

    var highestLevel = personalTax.progressiveTaxLevels().get("LEVEL_2");

    assertNotNull(highestLevel);
    assertEquals(TaxMapper.toBigDecimal(1_187_625), highestLevel);
  }

  // Lương thực nhận: 40M
  // Lương đóng BH: 36M (theo luật Việt Nam)
  // Không có người phụ thuộc
  @Test
  void calculateTax02() {
    var result =
        taxService.calculate(
            TaxRequestDTO.builder()
                .totalSalary(40_000_000L)
                .basicSalary(40_000_000L)
                .numberOfDependants(0)
                .build());

    var socialCoverage = result.insurance();

    assertEquals(TaxMapper.toBigDecimal(3_200_000.00), socialCoverage.socialInsurance());
    assertEquals(TaxMapper.toBigDecimal(600_000), socialCoverage.healthInsurance());
    assertEquals(TaxMapper.toBigDecimal(400_000), socialCoverage.unemploymentInsurance());
    assertEquals(TaxMapper.toBigDecimal(4_200_000), socialCoverage.totalInsurance());

    var personalTax = result.personalTax();

    assertEquals(TaxMapper.toBigDecimal(35_800_000), personalTax.pretaxSalary());
    assertEquals(TaxMapper.toBigDecimal(24_800_000), personalTax.taxableIncome());

    assertEquals(TaxMapper.toBigDecimal(3_310_000), personalTax.taxAmount());
    assertEquals(TaxMapper.toBigDecimal(0), personalTax.deductedAmount());
    assertEquals(TaxMapper.toBigDecimal(32_490_000), personalTax.netIncome());

    var highestLevel = personalTax.progressiveTaxLevels().get("LEVEL_3");

    assertNotNull(highestLevel);
    assertEquals(TaxMapper.toBigDecimal(1_360_000), highestLevel);
  }

  @Test
  void calculateTax03() {
    var result =
        taxService.calculate(
            TaxRequestDTO.builder()
                .totalSalary(12_000_000L)
                .basicSalary(11_000_000L)
                .numberOfDependants(0)
                .build());

    var socialCoverage = result.insurance();

    assertEquals(TaxMapper.toBigDecimal(880_000), socialCoverage.socialInsurance());
    assertEquals(TaxMapper.toBigDecimal(165_000), socialCoverage.healthInsurance());
    assertEquals(TaxMapper.toBigDecimal(110_000), socialCoverage.unemploymentInsurance());
    assertEquals(TaxMapper.toBigDecimal(1_155_000), socialCoverage.totalInsurance());

    var personalTax = result.personalTax();

    assertEquals(TaxMapper.toBigDecimal(10_845_000), personalTax.pretaxSalary());
    assertEquals(TaxMapper.toBigDecimal(0), personalTax.taxableIncome());

    assertEquals(TaxMapper.toBigDecimal(0), personalTax.taxAmount());
    assertEquals(TaxMapper.toBigDecimal(0), personalTax.deductedAmount());
    assertEquals(TaxMapper.toBigDecimal(10_845_000), personalTax.netIncome());

    assertTrue(personalTax.progressiveTaxLevels().isEmpty());
  }

  @Test
  void calculateTax04() {
    assertThrows(
        TaxRequestDTO.TaxCalculatorException.class,
        () ->
            taxService.calculate(
                TaxRequestDTO.builder()
                    .basicSalary(10_000_000L)
                    .totalSalary(8_000_000L)
                    .numberOfDependants(0)
                    .build()));
  }

  @Test
  void calculateTax05() {
    assertThrows(
        TaxRequestDTO.TaxCalculatorException.class,
        () ->
            taxService.calculate(
                TaxRequestDTO.builder()
                    .basicSalary(4_800_000)
                    .totalSalary(8_000_000L)
                    .numberOfDependants(0)
                    .build()));
  }
}
