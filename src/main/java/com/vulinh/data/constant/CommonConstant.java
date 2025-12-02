package com.vulinh.data.constant;

import module java.base;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstant {

  public static final String SECURITY_SCHEME_NAME = "bearerAuth";

  public static final UUID NIL_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

}
