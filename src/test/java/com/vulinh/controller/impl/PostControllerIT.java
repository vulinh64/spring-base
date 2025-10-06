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
class PostControllerIT {

  static final Set<String> TAGS = Set.of("integration test");

  @Autowired MockMvc mockMvc;

  @Autowired PostRepository postRepository;

  @Test
  @Transactional
  void testCreatePost() throws Exception {
    var postCreationRequest =
        new PostCreationRequest("Test Title", "Test Content", "test-slug", "Test Author", TAGS);

    var authorization =
        mockMvc
            .perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        JsonUtils.toMinimizedJSON(
                            UserLoginRequest.builder()
                                .username("admin")
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

    assertEquals(data.title(), postCreationRequest.title());
    assertEquals(data.slug(), postCreationRequest.slug());
    assertEquals(data.excerpt(), postCreationRequest.excerpt());

    var author = data.author();

    postRepository
        .findById(data.id())
        .ifPresentOrElse(
            post -> {
              assertEquals(post.getTitle(), postCreationRequest.title());
              assertEquals(post.getPostContent(), postCreationRequest.postContent());
              assertEquals(post.getSlug(), postCreationRequest.slug());
              assertEquals(author.username(), post.getAuthor().getUsername());
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
