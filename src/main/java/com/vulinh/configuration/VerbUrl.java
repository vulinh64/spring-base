package com.vulinh.configuration;

import org.springframework.http.HttpMethod;

public record VerbUrl(HttpMethod method, String url) {}
