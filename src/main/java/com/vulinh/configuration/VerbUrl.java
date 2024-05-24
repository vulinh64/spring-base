package com.vulinh.configuration;

import org.springframework.http.HttpMethod;

public record VerbUrl(HttpMethod method, String url) {

  public static VerbUrl of(HttpMethod method, String url) {
    return new VerbUrl(method, url);
  }
}
