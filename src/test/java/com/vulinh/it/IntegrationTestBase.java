package com.vulinh.it;

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

  protected static final String AUTH_USER = "admin";

  // spring.datasource.username
  protected static final String POSTGRES_USERNAME = "postgres";

  // spring.datasource.password
  protected static final String POSTGRES_PASSWORD = "123456";

  // spring.data.redis.password
  protected static final String REDIS_PASSWORD = POSTGRES_PASSWORD;

  protected static final String MOCK_UUID = "1234567890abcdef1234567890abcdef";

  protected static final String MOCK_SLUG = "test-title-%s".formatted(MOCK_UUID);

  @Container
  protected static final PostgreSQLContainer POSTGRESQL_CONTAINER =
      new PostgreSQLContainer("postgres:18.0-alpine3.22")
          .withUsername(POSTGRES_USERNAME)
          .withPassword(POSTGRES_PASSWORD)
          .waitingFor(HealthCheckCommand.POSTGRESQL.shellStrategyHealthCheck(POSTGRES_USERNAME));

  @Container
  protected static final RedisContainer REDIS_CONTAINER =
      new RedisContainer("redis:8.2.3-bookworm")
          .withCommand("redis-server", "--requirepass", REDIS_PASSWORD, "--save", "60", "1")
          .waitingFor(HealthCheckCommand.REDIS.shellStrategyHealthCheck(REDIS_PASSWORD));

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

  protected static <T> MockHttpServletRequestBuilder postWithEndpointAndPayload(
      String endpoint, T payload) {
    return post(endpoint)
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtils.toMinimizedJSON(payload));
  }

  // Recall this method to get the renewed JDBC url for each test class
  protected static void reinitializeConnectionPropertiesInternal(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
  }
}
