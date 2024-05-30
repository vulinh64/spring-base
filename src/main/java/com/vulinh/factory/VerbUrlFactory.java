package com.vulinh.factory;

import com.vulinh.configuration.VerbUrl;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;

@SuppressWarnings("java:S6548")
public enum VerbUrlFactory {
  INSTANCE;

  public VerbUrl of(@NonNull HttpMethod method, @NonNull String url) {
    return new VerbUrl(method, url);
  }
}
