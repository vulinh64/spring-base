package com.vulinh.controller.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import module java.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.redis.testcontainers.RedisContainer;
import com.vulinh.data.constant.EndpointConstant;
import com.vulinh.data.dto.carrier.TokenResponse;
import com.vulinh.data.dto.request.PostCreationRequest;
import com.vulinh.data.dto.request.UserLoginRequest;
import com.vulinh.data.dto.response.BasicPostResponse;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.entity.Tag;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.ShellStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@RequiredArgsConstructor
@Slf4j
class PostControllerIT {

  static final Set<String> TAGS = Set.of("integration test");

  static final String AUTH_USER = "admin";
  static final String TITLE = "Test Title";
  static final String EXCERPT = "Test Excerpt";
  static final String SLUG = "test-slug";

  @Autowired MockMvc mockMvc;

  @Autowired PostRepository postRepository;

  @Test
  @Transactional
  void testCreatePost() throws Exception {
    log.info("Test creating post...");

    var postCreationRequest =
        PostCreationRequest.builder()
            .title(TITLE)
            .excerpt(EXCERPT)
            .postContent("<script>Test Content</script>Test blank")
            .slug(SLUG)
            .tags(TAGS)
            .build();

    var authorization =
        mockMvc
            .perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        JsonUtils.toMinimizedJSON(
                            UserLoginRequest.builder()
                                .username(AUTH_USER)
                                .password("12345678")
                                .build())))
            .andExpect(status().isOk())
            .andReturn();

    var mvcResult =
        mockMvc
            .perform(
                post(EndpointConstant.ENDPOINT_POST)
                    .header(
                        "Authorization",
                        JsonUtils.toObject(
                                authorization.getResponse().getContentAsString(),
                                new TypeReference<GenericResponse<TokenResponse>>() {})
                            .data()
                            .accessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtils.delegate().writeValueAsBytes(postCreationRequest)))
            .andExpect(status().isCreated())
            .andReturn();

    var response =
        JsonUtils.toObject(
            mvcResult.getResponse().getContentAsString(),
            new TypeReference<GenericResponse<BasicPostResponse>>() {});

    assertEquals(ServiceErrorCode.MESSAGE_SUCCESS.getErrorCode(), response.errorCode());

    var data = response.data();

    assertEquals(TITLE, postCreationRequest.title());
    assertEquals(SLUG, postCreationRequest.slug());
    assertEquals(EXCERPT, postCreationRequest.excerpt());

    var author = data.author();

    postRepository
        .findById(data.id())
        .ifPresentOrElse(
            post -> {
              assertEquals(TITLE, post.getTitle());
              assertEquals(EXCERPT, post.getExcerpt());
              assertEquals("Test blank", post.getPostContent());
              assertEquals(SLUG, post.getSlug());
              assertEquals(AUTH_USER, author.username());
              assertTrue(
                  TAGS.containsAll(post.getTags().stream().map(Tag::getDisplayName).toList()));
            },
            () -> fail("Post was not found in the database"));
  }

  static final String TEST_REDIS_PASSWORD = "123456";

  @SuppressWarnings("resource")
  @Container
  static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
      new PostgreSQLContainer<>("postgres:18.0-alpine3.22")
          .waitingFor(HealthCheckCommand.POSTGRESQL.shellStrategyHealthCheck());

  @Container
  static final RedisContainer REDIS_CONTAINER =
      new RedisContainer("redis:8.2.3-bookworm")
          .withCommand("redis-server", "--requirepass", TEST_REDIS_PASSWORD, "--save", "60", "1")
          .waitingFor(HealthCheckCommand.REDIS.shellStrategyHealthCheck("123456"));

  @DynamicPropertySource
  static void dynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
    registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
    registry.add("spring.data.redis.port", REDIS_CONTAINER::getRedisPort);
  }

  @RequiredArgsConstructor
  public enum HealthCheckCommand {
    POSTGRESQL("pg_isready -U postgres"),
    REDIS("redis-cli -a %s ping");

    final String shellCommand;

    public ShellStrategy shellStrategyHealthCheck(Object... args) {
      return new ShellStrategy()
          .withCommand(ArrayUtils.isEmpty(args) ? shellCommand : shellCommand.formatted(args));
    }
  }
}
