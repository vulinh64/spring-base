package com.vulinh.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstant {

  public static final String MESSAGE_OK = "200";
  public static final String MESSAGE_CREATED = "201";
  public static final String MESSAGE_BAD_REQUEST = "400";
  public static final String MESSAGE_UNAUTHORIZED = "401";
  public static final String MESSAGE_FORBIDDEN = "403";
  public static final String MESSAGE_NOT_FOUND = "404";
  public static final String MESSAGE_INTERNAL_SERVER_CODE = "500";

  public static final String MESSAGE_INTERNAL_SERVER_SUMMARY = "Unknown server error";
  public static final String MESSAGE_USER_FIELD_INVALID = "Field validations failed";
  public static final String MESSAGE_NO_PERMISSION = "Insufficient privileges";

  public static final String MESSAGE_SUCCESS =
      """
      {
        "code": "M0000",
        "message": "Success"
      }
      """;

  public static final String MESSAGE_INTERNAL_SERVER_ERROR =
      """
      {
        "code": "M9999",
        "message": "Internal Server error, please contact the development team!"
      }
      """;
  public static final String MESSAGE_USER_CREATION_VALIDATION_FAILED =
      """
      {
        "code": "error code",
        "message": "error message"
      }
      """;

  public static final String UNCATEGORIZED_ID = "641ea43f-c426-49ad-bd7b-5ec551a6fa12";
}
