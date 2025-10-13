package com.vulinh.data.base;

import org.springframework.lang.NonNull;

public interface ApplicationError {

  @NonNull
  String getErrorCode();
}
