package com.vulinh.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseUtils {

  public static ResponseEntity<Void> returnOkOrNoContent(boolean result) {
    return ResponseEntity.status(result ? HttpStatus.OK : HttpStatus.NO_CONTENT).build();
  }
}
