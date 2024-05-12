package com.vulinh.controller;

import com.vulinh.constant.CommonConstant;
import com.vulinh.constant.EndpointConstant;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.auth.PasswordRequestDTO;
import com.vulinh.data.dto.auth.PasswordResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(EndpointConstant.ENDPOINT_PASSWORD)
@Tag(
    name = "Password Generator",
    description = "Public controller that helps generate BCrypt encoded password")
public interface BcryptPublicController {

  @PostMapping(EndpointConstant.BcryptEndpoint.GENERATE)
  @Operation(
      summary = "BCrypt Encoded Password Generator",
      description = "Generate encoded password from a raw string (8 characters or more)",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              value =
                                  """
                                  {
                                    "rawPassword": "Raw password"
                                  }
                                  """))),
      responses = {
        @ApiResponse(
            responseCode = CommonConstant.MESSAGE_OK,
            description = "BCrypt encoded password generated",
            content =
                @Content(
                    examples =
                        @ExampleObject(
                            value =
                                """
                                {
                                  "code": "M0000",
                                  "message": "Success",
                                  "data": {
                                    "rawPassword": "Raw password",
                                    "encodedPassword": "Bcrypt encoded password"
                                  }
                                }
                                """))),
        @ApiResponse(
            responseCode = CommonConstant.MESSAGE_BAD_REQUEST,
            description = "Password length is less than 8 characters",
            content =
                @Content(
                    examples =
                        @ExampleObject(
                            value =
                                """
                                {
                                  "code": "M0002",
                                  "message": "Invalid password"
                                }
                                """))),
        @ApiResponse(
            responseCode = CommonConstant.MESSAGE_INTERNAL_SERVER_CODE,
            description = CommonConstant.MESSAGE_INTERNAL_SERVER_SUMMARY,
            content =
                @Content(
                    examples =
                        @ExampleObject(value = CommonConstant.MESSAGE_INTERNAL_SERVER_ERROR)))
      })
  GenericResponse<PasswordResponseDTO> generateEncodedPassword(
      @RequestBody PasswordRequestDTO passwordRequestDTO);
}
