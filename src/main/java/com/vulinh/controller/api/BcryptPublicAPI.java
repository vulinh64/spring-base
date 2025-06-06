package com.vulinh.controller.api;

import com.vulinh.data.constant.CommonConstant;
import com.vulinh.data.constant.EndpointConstant;
import com.vulinh.data.dto.request.BCryptPasswordGenerationRequest;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.dto.response.PasswordResponse;
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
public interface BcryptPublicAPI {

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
                                  "errorCode": "M0000",
                                  "displayMessage": "Success",
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
  GenericResponse<PasswordResponse> generateEncodedPassword(
      @RequestBody BCryptPasswordGenerationRequest bcryptPasswordGenerationRequest);
}
