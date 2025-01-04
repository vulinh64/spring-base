package com.vulinh.configuration.cache;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class Common {

  public static <T> T logAndReturn(T any, String message, Object... args) {
    log.debug(message, args);
    return any;
  }
}
