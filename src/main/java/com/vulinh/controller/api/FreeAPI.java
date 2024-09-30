package com.vulinh.controller.api;

import com.vulinh.constant.EndpointConstant;
import com.vulinh.utils.springcron.*;
import com.vulinh.utils.springcron.dto.SpringCronGeneratorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;

@RequestMapping(EndpointConstant.ENDPOINT_FREE)
public interface FreeAPI {

  @GetMapping("/tax-calculator")
  Object calculateTax(
      @RequestParam Double totalSalary,
      @RequestParam Double basicSalary,
      @RequestParam(defaultValue = "0") Integer numberOfDependants);

  public static void main(String[] args) {
    System.out.print("Second expressions: ");

    System.out.print(
        Arrays.stream(SecondExpression.values())
            .map(Enum::name)
            .map("`%s`"::formatted)
            .collect(Collectors.joining(", ")));

    System.out.println();

    System.out.print("Minute expressions: ");

    System.out.print(
        Arrays.stream(MinuteExpression.values())
            .map(Enum::name)
            .map("`%s`"::formatted)
            .collect(Collectors.joining(", ")));

    System.out.println();

    System.out.print("Hour expressions: ");

    System.out.print(
        Arrays.stream(HourExpression.values())
            .map(Enum::name)
            .map("`%s`"::formatted)
            .collect(Collectors.joining(", ")));

    System.out.println();

    System.out.print("Day expressions: ");

    System.out.print(
        Arrays.stream(DayExpression.values())
            .map(Enum::name)
            .map("`%s`"::formatted)
            .collect(Collectors.joining(", ")));

    System.out.println();

    System.out.print("Month expressions: ");

    System.out.print(
        Arrays.stream(MonthExpression.values())
            .map(Enum::name)
            .map("`%s`"::formatted)
            .collect(Collectors.joining(", ")));

    System.out.println();

    System.out.print("Day of week expressions: ");

    System.out.print(
        Arrays.stream(DayOfWeekExpression.values())
            .map(Enum::name)
            .map("`%s`"::formatted)
            .collect(Collectors.joining(", ")));

    System.out.println();
  }

  @PostMapping("/spring-cron-generator")
  @Operation(
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description =
                  """
                  ## Second expressions:
                  - `EVERY_SECOND`
                  - `EVERY_SECOND_INTERVAL`
                  - `EVERY_SECOND_BETWEEN`
                  - `SPECIFIC_SECONDS`
                  - `EVERY_SECOND_INTERVAL_BETWEEN`

                  ## Minute expressions:
                  - `EVERY_MINUTE`
                  - `EVERY_MINUTE_INTERVAL`
                  - `EVERY_MINUTE_BETWEEN`
                  - `SPECIFIC_MINUTES`
                  - `EVERY_MINUTE_INTERVAL_BETWEEN`

                  ## Hour expressions:
                  - `EVERY_HOUR`
                  - `EVERY_HOUR_INTERVAL`
                  - `EVERY_HOUR_BETWEEN`
                  - `SPECIFIC_HOURS`
                  - `EVERY_HOUR_INTERVAL_BETWEEN`

                  ## Day expressions:
                  - `EVERY_DAY`
                  - `EVERY_DAY_INTERVAL`
                  - `EVERY_DAYS_BETWEEN`
                  - `SPECIFIC_DAYS`

                  ## Month expressions:
                  - `EVERY_MONTH`
                  - `EVERY_MONTHS_INTERVAL`
                  - `EVERY_MONTHS_BETWEEN`
                  - `SPECIFIC_MONTHS`

                  ## Day of week expressions:
                  - `EVERY_WEEK_DAY`
                  - `EVERY_WEEK_DAY_INTERVAL`
                  - `EVERY_WEEK_DAYS_BETWEEN`
                  - `SPECIFIC_WEEK_DAYS`
                  - `NTH_OCCURRENCE`
                  """,
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              value =
                                  """
                                  {
                                    "second": {
                                      "expression": "EVERY_SECOND_INTERVAL_BETWEEN",
                                      "arguments": [
                                        11,
                                        40,
                                        2
                                      ]
                                    },
                                    "minute": {
                                      "expression": "SPECIFIC_MINUTES",
                                      "arguments": [
                                        1,
                                        4,
                                        8,
                                        26
                                      ]
                                    },
                                    "hour": {
                                      "expression": "EVERY_HOUR_INTERVAL",
                                      "arguments": [
                                        3
                                      ]
                                    },
                                    "day": {
                                      "expression": "EVERY_DAYS_BETWEEN",
                                      "arguments": [
                                        15,
                                        28
                                      ]
                                    },
                                    "month": {
                                      "expression": "SPECIFIC_MONTHS",
                                      "arguments": [
                                        1,
                                        3,
                                        5,
                                        9,
                                        11,
                                        12
                                      ]
                                    },
                                    "dayOfWeek": {
                                      "expression": "NTH_OCCURRENCE",
                                      "arguments": [
                                        1,
                                        2
                                      ]
                                    }
                                  }
                                  """))))
  Object generateSpringCronExpression(@RequestBody SpringCronGeneratorDTO springCronGeneratorDTO);
}
