package com.vulinh.controller.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  @Container
  static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
      new PostgreSQLContainer<>("postgres:17.5-alpine");

  @DynamicPropertySource
  static void dynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
    registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
  }
}
