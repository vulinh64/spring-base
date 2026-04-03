package com.vulinh.it;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import module java.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vulinh.Constants;
import com.vulinh.data.constant.EndpointConstant;
import com.vulinh.data.dto.request.PostCreationRequest;
import com.vulinh.data.dto.response.BasicPostResponse;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.utils.ImageProperties;
import com.vulinh.utils.JsonUtils;
import com.vulinh.utils.KeycloakInitializationUtils;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;

@Slf4j
@TestMethodOrder(OrderAnnotation.class)
class PostCreationIT extends IntegrationTestBase {

  @Container
  public static final KeycloakContainer KEYCLOAK_CONTAINER =
      new KeycloakContainer(ImageProperties.KEYCLOAK.getFullImageName())
          .withAdminUsername(Constants.KC_ADMIN_USERNAME)
          .withAdminPassword(Constants.KC_ADMIN_PASSWORD)
          .waitingFor(ImageProperties.KEYCLOAK.shellStrategyHealthCheck());

  static final Set<String> TAGS = Set.of("integration test");
  static final String TITLE = "Test Title";
  static final String EXCERPT = "Test Excerpt";
  static final String POST_CONTENT = "Test blank";
  public static final PostCreationRequest VALID_POST_REQUEST =
      PostCreationRequest.builder()
          .title(TITLE)
          .excerpt(EXCERPT)
          .postContent(POST_CONTENT)
          .tags(TAGS)
          .build();
  static final String XSS_POST_CONTENT = "<script>evil()</script>" + POST_CONTENT;

  static UUID adminUserId;
  static UUID powerUserId;

  @Autowired JdbcTemplate jdbcTemplate;
  @Autowired Keycloak keycloak;

  @BeforeAll
  static void captureUserIds() {
    KeycloakInitializationUtils.initializeKeycloak(KEYCLOAK_CONTAINER);
    adminUserId = UUID.fromString(KeycloakInitializationUtils.adminUserId);
    powerUserId = UUID.fromString(KeycloakInitializationUtils.powerUserId);
  }

  @DynamicPropertySource
  static void initialize(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
    registry.add(
        "application-properties.keycloak-authentication.auth-server",
        KEYCLOAK_CONTAINER::getAuthServerUrl);
    registry.add("spring.rabbitmq.host", RABBIT_MQ_CONTAINER::getHost);
    registry.add("spring.rabbitmq.port", RABBIT_MQ_CONTAINER::getAmqpPort);
    registry.add("spring.rabbitmq.username", RABBIT_MQ_CONTAINER::getAdminUsername);
    registry.add("spring.rabbitmq.password", RABBIT_MQ_CONTAINER::getAdminPassword);
    registry.add(
        "application-properties.keycloak-authentication.username",
        KEYCLOAK_CONTAINER::getAdminUsername);
    registry.add(
        "application-properties.keycloak-authentication.password",
        KEYCLOAK_CONTAINER::getAdminPassword);
    registry.add("application-properties.security.realm-name", () -> Constants.KC_REALM);
    registry.add("application-properties.security.client-name", () -> Constants.KC_CLIENT);
  }

  @BeforeEach
  void setup() {
    purgeTestPost();
  }

  @AfterEach
  void cleanup() {
    purgeTestPost();
  }

  // post_revision has no FK to post, so must be deleted first.
  // post_tag_mapping has ON DELETE CASCADE on post_id, so it is handled automatically.
  private void purgeTestPost() {
    jdbcTemplate.update(
        "DELETE FROM post_revision WHERE post_id IN (SELECT id FROM post WHERE slug = ?)",
        Constants.MOCK_SLUG);
    jdbcTemplate.update("DELETE FROM post WHERE slug = ?", Constants.MOCK_SLUG);
  }

  @Test
  @Order(Integer.MIN_VALUE)
  void testKeycloakInitialization() {
    assertDoesNotThrow(() -> keycloak.realm(Constants.KC_REALM).toRepresentation());

    var clients = keycloak.realm(Constants.KC_REALM).clients().findByClientId(Constants.KC_CLIENT);

    assertFalse(clients.isEmpty(), "Client not found");

    assertEquals(3, keycloak.realm(Constants.KC_REALM).users().count());
  }

  @Test
  @SneakyThrows
  void testCreatePost_noAuthentication_returns401() {
    getMockMvc()
        .perform(postWithEndpointAndPayload(VALID_POST_REQUEST))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @SneakyThrows
  void testCreatePost_blankTitle_returnsInvalidTitleError() {
    var result =
        getMockMvc()
            .perform(
                postWithEndpointAndPayload(
                        PostCreationRequest.builder()
                            .title(StringUtils.EMPTY)
                            .excerpt(EXCERPT)
                            .postContent(POST_CONTENT)
                            .tags(TAGS)
                            .build())
                    .header(
                        HttpHeaders.AUTHORIZATION,
                        bearerToken(getAccessToken(Constants.TEST_ADMIN))))
            .andExpect(status().isBadRequest())
            .andReturn();

    var response =
        JsonUtils.toObject(
            result.getResponse().getContentAsString(),
            new TypeReference<GenericResponse<Object>>() {});

    assertEquals(ServiceErrorCode.MESSAGE_POST_INVALID_TITLE.getErrorCode(), response.errorCode());
  }

  @Test
  @SneakyThrows
  void testCreatePost_xssInPostContent_returnsXssViolation() {
    // Cannot use PostCreationRequest.builder() here: the compact constructor calls
    // TextSanitizer.validateAndPassThrough on postContent and throws XSSViolationException
    // on the test side before any HTTP call is made. Raw JSON bypasses the constructor.
    var rawPayload =
        """
        {"title":"%s","excerpt":"%s","postContent":"%s","tags":["%s"]}
        """
            .formatted(TITLE, EXCERPT, XSS_POST_CONTENT, TAGS.iterator().next())
            .strip();

    var result =
        getMockMvc()
            .perform(
                post(EndpointConstant.ENDPOINT_POST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(rawPayload)
                    .header(
                        HttpHeaders.AUTHORIZATION,
                        bearerToken(getAccessToken(Constants.TEST_ADMIN))))
            .andExpect(status().isBadRequest())
            .andReturn();

    var response =
        JsonUtils.toObject(
            result.getResponse().getContentAsString(),
            new TypeReference<GenericResponse<Object>>() {});

    assertEquals(ServiceErrorCode.MESSAGE_XSS_VIOLATION.getErrorCode(), response.errorCode());
  }

  @Test
  @SneakyThrows
  void testCreatePost_validRequest_createsSuccessfully() {
    var result =
        getMockMvc()
            .perform(
                postWithEndpointAndPayload(VALID_POST_REQUEST)
                    .header(
                        HttpHeaders.AUTHORIZATION,
                        bearerToken(getAccessToken(Constants.TEST_ADMIN))))
            .andExpect(status().isCreated())
            .andReturn();

    var response =
        JsonUtils.toObject(
            result.getResponse().getContentAsString(),
            new TypeReference<GenericResponse<BasicPostResponse>>() {});

    assertEquals(ServiceErrorCode.MESSAGE_SUCCESS.getErrorCode(), response.errorCode());

    var data = response.data();

    assertEquals(TITLE, data.title());
    assertEquals(EXCERPT, data.excerpt());
    assertEquals(Constants.MOCK_SLUG, data.slug());
  }

  @Test
  @SneakyThrows
  void testEditPost_adminCannotEditPowerUserPost_returns403() {
    // Stateless SQL insertion: seed a post owned by the power user.
    // purgeTestPost() in @BeforeEach already cleared any leftover with the same slug.
    jdbcTemplate.update(
        """
        INSERT INTO post (id, title, slug, excerpt, post_content, author_id)
        VALUES (gen_random_uuid(), ?, ?, ?, ?, ?)
        """,
        TITLE,
        Constants.MOCK_SLUG,
        EXCERPT,
        POST_CONTENT,
        powerUserId);

    var postId =
        jdbcTemplate.queryForObject(
            "SELECT id FROM post WHERE slug = ?", UUID.class, Constants.MOCK_SLUG);

    getMockMvc()
        .perform(
            patch("/post/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.toMinimizedJSON(VALID_POST_REQUEST))
                .header(
                    HttpHeaders.AUTHORIZATION,
                    bearerToken(getAccessToken(Constants.TEST_NORMAL_USER))))
        .andExpect(status().isForbidden());
  }

  private static String bearerToken(String accessToken) {
    return "Bearer %s".formatted(accessToken);
  }
}
