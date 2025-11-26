package com.vulinh.controller.api;

import module java.base;

import com.vulinh.data.constant.CommonConstant;
import com.vulinh.data.constant.EndpointConstant;
import com.vulinh.data.constant.EndpointConstant.AuthEndpoint;
import com.vulinh.data.dto.carrier.TokenResponse;
import com.vulinh.data.dto.request.PasswordChangeRequest;
import com.vulinh.data.dto.request.RefreshTokenRequest;
import com.vulinh.data.dto.request.UserLoginRequest;
import com.vulinh.data.dto.request.UserRegistrationRequest;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.dto.response.SingleUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(EndpointConstant.ENDPOINT_AUTH)
@Tag(
    name = "Authentication controller",
    description =
        "Public controller that allows user login, user registration and user confirmation")
public interface AuthAPI {

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

  @Deprecated(forRemoval = true)
  @PostMapping(AuthEndpoint.REGISTER)
  @Operation(
      summary = "User Registration",
      description = "Register a new user",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              value =
                                  """
                                  {
                                    "username": "username",
                                    "password": "12345678",
                                    "email": "email@company.com",
                                    "fullName": "Name"
                                  }
                                  """))),
      responses = {
        @ApiResponse(
            responseCode = CommonConstant.MESSAGE_CREATED,
            description = "User created",
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
                                    "id": "user-uuid-here",
                                    "username": "username",
                                    "fullName": "Name",
                                    "email": "email@company.com",
                                    "createdDate": "ISO Date and Time",
                                    "updatedDate": "ISO Date and Time"
                                  }
                                }
                                """))),
        @ApiResponse(
            responseCode = CommonConstant.MESSAGE_BAD_REQUEST,
            description = CommonConstant.MESSAGE_USER_FIELD_INVALID,
            content =
                @Content(
                    examples =
                        @ExampleObject(
                            value = CommonConstant.MESSAGE_USER_CREATION_VALIDATION_FAILED))),
        @ApiResponse(
            responseCode = CommonConstant.MESSAGE_INTERNAL_SERVER_CODE,
            description = CommonConstant.MESSAGE_INTERNAL_SERVER_SUMMARY,
            content =
                @Content(
                    examples =
                        @ExampleObject(value = CommonConstant.MESSAGE_INTERNAL_SERVER_ERROR)))
      })
  @ResponseStatus(HttpStatus.CREATED)
  GenericResponse<SingleUserResponse> register(
      @RequestBody UserRegistrationRequest userRegistrationRequest);

  @Deprecated(forRemoval = true)
  @GetMapping(AuthEndpoint.CONFIRM_USER)
  @Operation(
      summary = "Confirm user registration",
      description = "Confirm the user registration via a public link",
      responses = {
        @ApiResponse(
            responseCode = CommonConstant.MESSAGE_OK,
            description = "User registered successfully",
            content = @Content(examples = @ExampleObject(value = CommonConstant.MESSAGE_SUCCESS))),
        @ApiResponse(
            responseCode = CommonConstant.MESSAGE_NOT_FOUND,
            description = "Invalid user or confirmation code",
            content =
                @Content(
                    examples =
                        @ExampleObject(
                            value =
                                """
                                {
                                  "errorCode": "M0405",
                                  "displayMessage": "Invalid user confirmation"
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
  ResponseEntity<GenericResponse<Object>> confirmUser(
      @RequestParam UUID userId, @RequestParam String code);

  @Deprecated(forRemoval = true)
  @PatchMapping(AuthEndpoint.CHANGE_PASSWORD)
  ResponseEntity<Object> changePassword(
      @RequestBody PasswordChangeRequest passwordChangeRequest,
      HttpServletRequest httpServletRequest);

  @Deprecated(forRemoval = true)
  @PostMapping(AuthEndpoint.REFRESH_TOKEN)
  GenericResponse<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest);

  @Deprecated(forRemoval = true)
  @DeleteMapping(AuthEndpoint.LOG_OUT)
  ResponseEntity<Void> logout(
      @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorization);
}
