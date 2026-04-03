package com.vulinh;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

  // spring.datasource.username
  public static final String POSTGRES_USERNAME = "postgres";

  // spring.datasource.password
  public static final String POSTGRES_PASSWORD = "123456";

  public static final String COMMON_PASSWORD = "123456";

  // Keycloak Section
  public static final String KC_REALM = "spring-base-test";
  public static final String KC_CLIENT = "spring-base-test-client";
  public static final String MOCK_SLUG = "test-title";
  public static final String KC_ADMIN_USERNAME = "administrator";
  public static final String KC_ADMIN_PASSWORD = "administrator";
  public static final String TEST_ADMIN = "admin";
  public static final String TEST_POWER_USER = "power_user";
  public static final String TEST_NORMAL_USER = "normal";
}
