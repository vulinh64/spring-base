package com.vulinh.data.constant;

import module java.base;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstant {

  public static final String MESSAGE_OK = "200";
  public static final String MESSAGE_UNAUTHORIZED = "401";
  public static final String MESSAGE_INTERNAL_SERVER_CODE = "500";

  public static final String MESSAGE_INTERNAL_SERVER_SUMMARY = "Unknown server error";

  public static final String MESSAGE_INTERNAL_SERVER_ERROR =
      """
      {
        "code": "M9999",
        "message": "Internal Server error, please contact the development team!"
      }
      """;

  public static final UUID NIL_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

  public static final String POST_ENTITY = "Post";
  public static final String COMMENT_ENTITY = "Comment";
  public static final String CATEGORY_ENTITY = "Category";
  public static final String POST_REVISION_ENTITY = "Post Revision";
}
