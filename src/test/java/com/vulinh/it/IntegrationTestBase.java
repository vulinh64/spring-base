package com.vulinh.it;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import module java.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.redis.testcontainers.RedisContainer;
import com.vulinh.configuration.data.ApplicationProperties;
import com.vulinh.data.constant.EndpointConstant;
import com.vulinh.data.constant.EndpointConstant.AuthEndpoint;
import com.vulinh.data.constant.UserRole;
import com.vulinh.data.dto.carrier.TokenResponse;
import com.vulinh.data.dto.request.UserLoginRequest;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.utils.HealthCheckCommand;
import com.vulinh.utils.JsonUtils;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@Slf4j
public abstract class IntegrationTestBase {

  // spring.datasource.username
  protected static final String POSTGRES_USERNAME = "postgres";

  // spring.datasource.password
  protected static final String POSTGRES_PASSWORD = "123456";

  protected static final String MOCK_UUID = "1234567890abcdef1234567890abcdef";

  protected static final String MOCK_SLUG = "test-title-%s".formatted(MOCK_UUID);

  protected static final String COMMON_PASSWORD = "123456";

  protected static final String ADMIN_USER = "admin";

  protected static final String POWER_USER = "power_user";

  @Container
  protected static final PostgreSQLContainer POSTGRESQL_CONTAINER =
      new PostgreSQLContainer("postgres:18.0-alpine3.22")
          .withUsername(POSTGRES_USERNAME)
          .withPassword(POSTGRES_PASSWORD)
          .waitingFor(HealthCheckCommand.POSTGRESQL.shellStrategyHealthCheck(POSTGRES_USERNAME));

  @Container
  protected static final RedisContainer REDIS_CONTAINER =
      new RedisContainer("redis:8.2.3-bookworm")
          .withCommand("redis-server", "--save", "60", "1")
          .waitingFor(HealthCheckCommand.REDIS.shellStrategyHealthCheck());

  @Container
  protected static final KeycloakContainer KEYCLOAK_CONTAINER =
      new KeycloakContainer("quay.io/keycloak/keycloak:26.4")
          .withAdminUsername("admin")
          .withAdminPassword("123456")
          .waitingFor(HealthCheckCommand.KEYCLOAK.shellStrategyHealthCheck());

  @Autowired private MockMvc mockMvc;

  @Autowired private ApplicationProperties applicationProperties;

  // Get the f*** out of my face, checked exceptions
  @SneakyThrows
  protected String getAccessToken(String username) {
    var adminLoginResult =
        mockMvc
            .perform(
                postWithEndpointAndPayload(
                    EndpointConstant.ENDPOINT_AUTH + AuthEndpoint.LOGIN,
                    UserLoginRequest.builder()
                        .username(username)
                        .password(COMMON_PASSWORD)
                        .build()))
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

  @SneakyThrows
  void initializeKeycloak() {
    var clientIdMap = new AtomicReference<String>();

    var security = applicationProperties.security();

    var replacementMap =
        Map.ofEntries(
            Map.entry("%%KEYCLOAK_REALM%%", security.realmName()),
            Map.entry("%%CLIENT_ID%%", security.clientName()),
            Map.entry("%%ROLE_ADMIN%%", UserRole.ADMIN.name()),
            Map.entry("%%ROLE_POWER_USER%%", UserRole.POWER_USER.name()),
            Map.entry("%%ADMIN_USERNAME%%", ADMIN_USER),
            Map.entry("%%POWER_USER_USERNAME%%", POWER_USER));

    var commands = KeycloakShellCommandUtils.readKeycloakExecCommands(replacementMap);

    for (var command : commands) {
      var possibleUuid = clientIdMap.get();

      if (possibleUuid != null) {
        command =
            Arrays.stream(command)
                .map(s -> s.replace("%%CLIENT_UUID%%", possibleUuid))
                .toArray(String[]::new);
      }

      var result = KEYCLOAK_CONTAINER.execInContainer(command);

      var output = StringUtils.defaultIfBlank(result.getStdout(), result.getStderr());

      log.info(output);

      if (String.join(" ", command).contains("create clients -r")) {
        clientIdMap.set(output.replace("\r", StringUtils.EMPTY).replace("\n", StringUtils.EMPTY));
      }
    }
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
    registry.add(
        "application-properties.security.auth-server", KEYCLOAK_CONTAINER::getAuthServerUrl);
  }
}
