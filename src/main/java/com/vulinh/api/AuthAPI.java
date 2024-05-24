package com.vulinh.api;

import com.vulinh.constant.CommonConstant;
import com.vulinh.constant.EndpointConstant;
import com.vulinh.constant.EndpointConstant.AuthEndpoint;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.auth.PasswordChangeDTO;
import com.vulinh.data.dto.auth.UserLoginDTO;
import com.vulinh.data.dto.auth.UserRegistrationDTO;
import com.vulinh.data.dto.security.AccessToken;
import com.vulinh.data.dto.user.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(EndpointConstant.ENDPOINT_AUTH)
@Tag(
    name = "Authentication controller",
    description =
        "Public controller that allows user login, user registration and user confirmation")
public interface AuthAPI {

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
                                  "code": "M0000",
                                  "message": "Success",
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
                                  "code": "M0401",
                                  "message": "Invalid credentials"
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
  GenericResponse<AccessToken> login(@RequestBody UserLoginDTO userLoginDTO);

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
                                  "code": "M0000",
                                  "message": "Success",
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
  GenericResponse<UserDTO> register(@RequestBody UserRegistrationDTO userRegistrationDTO);

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
                                  "code": "M0405",
                                  "message": "Invalid user confirmation"
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
      @RequestParam String userId, @RequestParam String code);

  @PatchMapping(AuthEndpoint.CHANGE_PASSWORD)
  ResponseEntity<Object> changePassword(
      @RequestBody PasswordChangeDTO passwordChangeDTO,
      HttpServletRequest httpServletRequest);
}
