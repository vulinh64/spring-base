package com.vulinh.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.vulinh.Constants;
import com.vulinh.configuration.data.ApplicationProperties;
import com.vulinh.keycloak.KeycloakAuthExchange;
import com.vulinh.utils.HealthCheckCommand;
import com.vulinh.utils.JsonUtils;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
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

  @Container
  protected static final PostgreSQLContainer POSTGRESQL_CONTAINER =
      new PostgreSQLContainer("postgres:18.0-alpine3.22")
          .withUsername(Constants.POSTGRES_USERNAME)
          .withPassword(Constants.POSTGRES_PASSWORD)
          .waitingFor(
              HealthCheckCommand.POSTGRESQL.shellStrategyHealthCheck(Constants.POSTGRES_USERNAME));

  @Container
  protected static final KeycloakContainer KEYCLOAK_CONTAINER =
      new KeycloakContainer("quay.io/keycloak/keycloak:26.4")
          .withAdminUsername(Constants.KC_ADMIN_USERNAME)
          .withAdminPassword(Constants.KC_ADMIN_PASSWORD)
          .waitingFor(HealthCheckCommand.KEYCLOAK.shellStrategyHealthCheck());

  @Autowired private MockMvc mockMvc;

  @Autowired private ApplicationProperties applicationProperties;

  @Autowired private KeycloakAuthExchange keycloakAuthExchange;

  // Get the f*** out of my face, checked exceptions
  protected String getAccessToken(String username) {
    return keycloakAuthExchange
        .getToken(
            "password",
            applicationProperties.security().clientName(),
            username,
            Constants.COMMON_PASSWORD)
        .accessToken();
  }

  protected static <T> MockHttpServletRequestBuilder postWithEndpointAndPayload(
      String endpoint, T payload) {
    return post(endpoint)
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtils.toMinimizedJSON(payload));
  }
}
