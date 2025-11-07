package com.vulinh.service.taxcalculator;

import static org.junit.jupiter.api.Assertions.*;

import com.vulinh.service.taxcalculator.TaxSupport.TaxCalculatorException;
import org.junit.jupiter.api.Test;

class TaxServiceTest {

  static final TaxMapper TAX_MAPPER = TaxMapper.INSTANCE;

  final TaxService taxService = new TaxService();

  // Lương thực nhận: 34M
  // Lương đóng BH: 6.5M
  // Có 1 người phụ thuộc
  @Test
  void calculateTax01() {
    var result =
        taxService.calculate(
            TaxRequest.builder()
                .totalSalary(34_000_000L)
                .basicSalary(6_500_000L)
                .numberOfDependants(1)
                .build());

    var socialCoverage = result.insurance();

    assertEquals(TAX_MAPPER.toBigDecimal(520_000), socialCoverage.socialInsurance());
    assertEquals(TAX_MAPPER.toBigDecimal(97_500), socialCoverage.healthInsurance());
    assertEquals(TAX_MAPPER.toBigDecimal(65_000), socialCoverage.unemploymentInsurance());
    assertEquals(TAX_MAPPER.toBigDecimal(682_500), socialCoverage.totalInsurance());

    var personalTax = result.personalTax();

    assertEquals(TAX_MAPPER.toBigDecimal(33_317_500), personalTax.pretaxSalary());
    assertEquals(TAX_MAPPER.toBigDecimal(17_917_500), personalTax.taxableIncome());
    assertEquals(TAX_MAPPER.toBigDecimal(1_937_625), personalTax.taxAmount());
    assertEquals(TAX_MAPPER.toBigDecimal(4_400_000), personalTax.deductedAmount());
    assertEquals(TAX_MAPPER.toBigDecimal(31_379_875), personalTax.netIncome());

    var highestLevel = personalTax.progressiveTaxLevels().get(2);

    assertNotNull(highestLevel);
    assertEquals(TAX_MAPPER.toBigDecimal(1_187_625), highestLevel);
  }

  // Lương thực nhận: 40M
  // Lương đóng BH: 36M (theo luật Việt Nam)
  // Không có người phụ thuộc
  @Test
  void calculateTax02() {
    var result =
        taxService.calculate(
            TaxRequest.builder()
                .totalSalary(40_000_000L)
                .basicSalary(40_000_000L)
                .numberOfDependants(0)
                .build());

    var socialCoverage = result.insurance();

    assertEquals(TAX_MAPPER.toBigDecimal(3_200_000.00), socialCoverage.socialInsurance());
    assertEquals(TAX_MAPPER.toBigDecimal(600_000), socialCoverage.healthInsurance());
    assertEquals(TAX_MAPPER.toBigDecimal(400_000), socialCoverage.unemploymentInsurance());
    assertEquals(TAX_MAPPER.toBigDecimal(4_200_000), socialCoverage.totalInsurance());

    var personalTax = result.personalTax();

    assertEquals(TAX_MAPPER.toBigDecimal(35_800_000), personalTax.pretaxSalary());
    assertEquals(TAX_MAPPER.toBigDecimal(24_800_000), personalTax.taxableIncome());

    assertEquals(TAX_MAPPER.toBigDecimal(3_310_000), personalTax.taxAmount());
    assertEquals(TAX_MAPPER.toBigDecimal(0), personalTax.deductedAmount());
    assertEquals(TAX_MAPPER.toBigDecimal(32_490_000), personalTax.netIncome());

    var highestLevel = personalTax.progressiveTaxLevels().get(3);

    assertNotNull(highestLevel);
    assertEquals(TAX_MAPPER.toBigDecimal(1_360_000), highestLevel);
  }

  @Test
  void calculateTax03() {
    var result =
        taxService.calculate(
            TaxRequest.builder()
                .totalSalary(12_000_000L)
                .basicSalary(11_000_000L)
                .numberOfDependants(0)
                .build());

    var socialCoverage = result.insurance();

    assertEquals(TAX_MAPPER.toBigDecimal(880_000), socialCoverage.socialInsurance());
    assertEquals(TAX_MAPPER.toBigDecimal(165_000), socialCoverage.healthInsurance());
    assertEquals(TAX_MAPPER.toBigDecimal(110_000), socialCoverage.unemploymentInsurance());
    assertEquals(TAX_MAPPER.toBigDecimal(1_155_000), socialCoverage.totalInsurance());

    var personalTax = result.personalTax();

    assertEquals(TAX_MAPPER.toBigDecimal(10_845_000), personalTax.pretaxSalary());
    assertEquals(TAX_MAPPER.toBigDecimal(0), personalTax.taxableIncome());

    assertEquals(TAX_MAPPER.toBigDecimal(0), personalTax.taxAmount());
    assertEquals(TAX_MAPPER.toBigDecimal(0), personalTax.deductedAmount());
    assertEquals(TAX_MAPPER.toBigDecimal(10_845_000), personalTax.netIncome());

    assertTrue(personalTax.progressiveTaxLevels().isEmpty());
  }

  @Test
  void calculateTax04() {
    assertThrows(
        TaxCalculatorException.class,
        () ->
            taxService.calculate(
                TaxRequest.builder()
                    .basicSalary(10_000_000L)
                    .totalSalary(8_000_000L)
                    .numberOfDependants(0)
                    .build()));
  }

  @Test
  void calculateTax05() {
    assertThrows(
        TaxCalculatorException.class,
        () ->
            taxService.calculate(
                TaxRequest.builder()
                    .basicSalary(4_800_000)
                    .totalSalary(8_000_000L)
                    .numberOfDependants(0)
                    .build()));
  }
}
