package com.vulinh.controller;

import com.vulinh.constant.CommonConstant;
import com.vulinh.constant.EndpointConstant;
import com.vulinh.constant.EndpointConstant.UserEndpoint;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.auth.UserRegistrationDTO;
import com.vulinh.data.dto.user.UserBasicDTO;
import com.vulinh.data.dto.user.UserDTO;
import com.vulinh.data.dto.user.UserSearchDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(EndpointConstant.ENDPOINT_USER)
@Tag(
    name = "User management controller",
    description = "Support various user management operations")
public interface UserAPI {

  @PostMapping(UserEndpoint.CREATE_USER)
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "User Direct Creation",
      description = "Directly create an active user (`ADMIN` only)",
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
                                    "password": "password",
                                    "email": "email@site.com",
                                    "fullName": "Full name",
                                    "userRoles": [
                                      "ADMIN",
                                      "POWER_USER",
                                      "USER"
                                    ]
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
                                    "username": "Username",
                                    "fullName": "Full name",
                                    "email": "mail@site.com",
                                    "isActive": true,
                                    "createdDate": "ISO Date and Time",
                                    "updatedDate": "ISO Date and Time",
                                    "userRoles": [
                                      {
                                        "id": "ROLE_NAME",
                                        "superiority": 0
                                      }
                                    ]
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
  GenericResponse<UserDTO> createUser(@RequestBody UserRegistrationDTO userRegistrationDTO);

  @DeleteMapping(UserEndpoint.DELETE_USER + "/{id}")
  @Operation(
      summary = "User Deletion",
      description = "Delete a user (require `ADMIN` privilege)",
      responses = {
        @ApiResponse(
            responseCode = CommonConstant.MESSAGE_FORBIDDEN,
            description = "Users cannot delete themselves",
            content =
                @Content(
                    examples =
                        @ExampleObject(
                            value =
                                """
                                {
                                  "code": "M0010",
                                  "message": "Cannot delete self"
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
  ResponseEntity<Void> deleteUser(@PathVariable String id, HttpServletRequest httpServletRequest);

  @GetMapping(UserEndpoint.DETAILS)
  @Operation(
      summary = "User basic detail",
      description = "Get current user's basic detail",
      responses = {
        @ApiResponse(
            responseCode = CommonConstant.MESSAGE_OK,
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
                                    "username": "user-name-here",
                                    "fullName": "full-name-here",
                                    "email": "user-email@company.com",
                                    "createdDate": "ISO  and Time",
                                    "updatedDate": "ISO Date and Time",
                                    "userRoles": [
                                      "user-role-here"
                                    ]
                                  }
                                }
                                """))),
        @ApiResponse(responseCode = CommonConstant.MESSAGE_FORBIDDEN),
        @ApiResponse(
            responseCode = CommonConstant.MESSAGE_INTERNAL_SERVER_CODE,
            description = CommonConstant.MESSAGE_INTERNAL_SERVER_SUMMARY,
            content =
                @Content(
                    examples =
                        @ExampleObject(value = CommonConstant.MESSAGE_INTERNAL_SERVER_ERROR)))
      })
  ResponseEntity<GenericResponse<UserBasicDTO>> getSelfDetail(
      HttpServletRequest httpServletRequest);

  @GetMapping(UserEndpoint.SEARCH)
  @Operation(
      summary = "Search user via identities and roles",
      description = "Search user via identity (id, username, email or full name) and roles",
      parameters = {
        @Parameter(
            in = ParameterIn.QUERY,
            name = "identity",
            description = "Identity to search (id, username, email or full name)"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "roles",
            description = "List of roles to search (separated by comma character)")
      },
      responses = {
        @ApiResponse(
            responseCode = CommonConstant.MESSAGE_OK,
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
                                    "content": [
                                      {
                                        "id": "user-uuid-here",
                                        "username": "username",
                                        "fullName": "Full Name",
                                        "email": "email@company.com",
                                        "isActive": true,
                                        "createdDate": "ISO Date and Time",
                                        "updatedDate": "ISO Date and Time",
                                        "userRoles": [
                                          {
                                            "id": "ROLE_ID",
                                            "superiority": 0
                                          }
                                        ]
                                      }
                                    ],
                                    "pageable": {
                                      "pageNumber": 0,
                                      "pageSize": 20,
                                      "sort": {
                                        "empty": true,
                                        "sorted": false,
                                        "unsorted": true
                                      },
                                      "offset": 0,
                                      "paged": true,
                                      "unpaged": false
                                    },
                                    "last": true,
                                    "totalElements": 1,
                                    "totalPages": 1,
                                    "first": true,
                                    "size": 20,
                                    "number": 0,
                                    "sort": {
                                      "empty": true,
                                      "sorted": false,
                                      "unsorted": true
                                    },
                                    "numberOfElements": 1,
                                    "empty": false
                                  }
                                }
                                """))),
        @ApiResponse(
            responseCode = CommonConstant.MESSAGE_FORBIDDEN,
            description = CommonConstant.MESSAGE_NO_PERMISSION,
            content =
                @Content(
                    examples =
                        @ExampleObject(
                            value =
                                """
                                {
                                    "code": "M0403",
                                    "message": "Invalid authorization info"
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
  GenericResponse<Page<UserDTO>> search(
      @ParameterObject UserSearchDTO userSearchDTO, @ParameterObject Pageable pageable);
}
