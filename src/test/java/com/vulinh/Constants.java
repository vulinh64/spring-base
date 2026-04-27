package com.vulinh;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

  // spring.datasource.username
  public static final String POSTGRES_USERNAME = "postgres";

  // spring.datasource.password
  public static final String POSTGRES_PASSWORD = "123456";

  public static final String MOCK_SLUG = "test-title";
}
