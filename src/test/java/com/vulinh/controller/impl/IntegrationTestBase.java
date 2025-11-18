package com.vulinh.controller.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.redis.testcontainers.RedisContainer;
import com.vulinh.data.constant.EndpointConstant;
import com.vulinh.data.constant.EndpointConstant.AuthEndpoint;
import com.vulinh.data.dto.carrier.TokenResponse;
import com.vulinh.data.dto.request.UserLoginRequest;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.utils.HealthCheckCommand;
import com.vulinh.utils.JsonUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@Getter
public abstract class IntegrationTestBase {

  public static final String AUTH_USER = "admin";

  static final String TEST_POSTGRES_USER = "postgres";
  static final String TEST_POSTGRES_PASSWORD = "123465";
  static final String TEST_REDIS_PASSWORD = "123456";

  @Container
  static final PostgreSQLContainer POSTGRESQL_CONTAINER =
      new PostgreSQLContainer("postgres:18.0-alpine3.22")
          .withUsername(TEST_POSTGRES_USER)
          .withPassword(TEST_POSTGRES_PASSWORD)
          .waitingFor(HealthCheckCommand.POSTGRESQL.shellStrategyHealthCheck(TEST_POSTGRES_USER));

  @Container
  static final RedisContainer REDIS_CONTAINER =
      new RedisContainer("redis:8.2.3-bookworm")
          .withCommand("redis-server", "--requirepass", TEST_REDIS_PASSWORD, "--save", "60", "1")
          .waitingFor(HealthCheckCommand.REDIS.shellStrategyHealthCheck("123456"));

  @DynamicPropertySource
  static void dynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
    registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
  }

  // Stupid hack to reuse PostgreSQL container from parent class
  static void reinitializeJdbcUrlInternal(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
    registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
    registry.add("spring.data.redis.port", REDIS_CONTAINER::getRedisPort);
  }

  static <T> MockHttpServletRequestBuilder postWithEndpointAndPayload(String endpoint, T payload) {
    return post(endpoint)
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtils.toMinimizedJSON(payload));
  }

  @Autowired private MockMvc mockMvc;

  // Get the f*** out of my face, checked exceptions
  @SneakyThrows
  protected String getAdminAccessToken() {
    var adminLoginResult =
        mockMvc
            .perform(
                postWithEndpointAndPayload(
                    EndpointConstant.ENDPOINT_AUTH + AuthEndpoint.LOGIN,
                    UserLoginRequest.builder().username(AUTH_USER).password("12345678").build()))
            .andExpect(status().isOk())
            .andReturn();

    assertNotNull(adminLoginResult);

    var response = adminLoginResult.getResponse();

    assertNotNull(response);

    return JsonUtils.toObject(
            response.getContentAsString(), new TypeReference<GenericResponse<TokenResponse>>() {})
        .data()
        .accessToken();
  }
}
