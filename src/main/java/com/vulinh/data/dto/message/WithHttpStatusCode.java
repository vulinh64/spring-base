package com.vulinh.data.dto.message;

import org.springframework.http.HttpStatusCode;

public interface WithHttpStatusCode extends I18NCapable {

  HttpStatusCode getHttpStatusCode();
}
