package com.vulinh.data.dto.message;

import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;

public interface WithHttpStatusCode extends I18NCapable {

  @NonNull
  HttpStatusCode getHttpStatusCode();
}
