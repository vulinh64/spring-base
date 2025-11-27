package com.vulinh.controller.api;

import com.vulinh.data.constant.CommonConstant;
import com.vulinh.data.constant.EndpointConstant;
import com.vulinh.data.constant.EndpointConstant.AuthEndpoint;
import com.vulinh.data.dto.carrier.TokenResponse;
import com.vulinh.data.dto.request.UserLoginRequest;
import com.vulinh.data.dto.response.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RequestMapping(EndpointConstant.ENDPOINT_AUTH)
@Tag(
    name = "Authentication controller",
    description =
        "Public controller that allows user login, user registration and user confirmation")
public interface AuthAPI {

  /**
   * @deprecated Dedicated to Keycloak authentication soon
   */
  @Deprecated(forRemoval = true)
  @PostMapping(AuthEndpoint.LOGIN)
  @Operation(
      summary = "User login",
      description = "Login with username and password",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              value =
                                  """
                                  {
                                    "username": "admin",
                                    "password": "12345678"
                                  }
                                  """))),
      responses = {
        @ApiResponse(
            responseCode = CommonConstant.MESSAGE_OK,
            description = "Authentication successfully",
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
                                    "accessToken": "your-access-token",
                                    "issuedAt": "ISO Date and Time",
                                    "expiration": "ISO Date and Time"
                                  }
                                }
                                """))),
        @ApiResponse(
            responseCode = CommonConstant.MESSAGE_UNAUTHORIZED,
            description = "Invalid credentials",
            content =
                @Content(
                    examples =
                        @ExampleObject(
                            value =
                                """
                                {
                                  "errorCode": "M0401",
                                  "displayMessage": "Invalid credentials"
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
  GenericResponse<TokenResponse> login(@RequestBody UserLoginRequest userLoginRequest);
}
