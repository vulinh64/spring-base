package com.vulinh.it;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import module java.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vulinh.data.constant.EndpointConstant;
import com.vulinh.data.dto.request.PostCreationRequest;
import com.vulinh.data.dto.response.BasicPostResponse;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.entity.QPost;
import com.vulinh.data.entity.Tag;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.utils.JsonUtils;
import com.vulinh.utils.post.NoDashedUUIDGenerator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@TestMethodOrder(OrderAnnotation.class)
class PostCreationIT extends IntegrationTestBase {

  static final Set<String> TAGS = Set.of("integration test");

  static final String TITLE = "Test Title";
  static final String EXCERPT = "Test Excerpt";

  @Autowired PostRepository postRepository;

  @Test
  @Transactional
  @Commit
  @Order(0)
  @SneakyThrows
  void testCreatePost() {
    var accessToken = getAdminAccessToken();

    var postCreationResult =
        createPostRequest(
            PostCreationRequest.builder()
                .title(TITLE)
                .excerpt(EXCERPT)
                .postContent("<script>Test Content</script>Test blank")
                .tags(TAGS)
                .build(),
            accessToken);

    var response =
        JsonUtils.toObject(
            postCreationResult.getResponse().getContentAsString(),
            new TypeReference<GenericResponse<BasicPostResponse>>() {});

    assertEquals(ServiceErrorCode.MESSAGE_SUCCESS.getErrorCode(), response.errorCode());

    var data = response.data();

    assertEquals(TITLE, data.title());
    assertEquals(EXCERPT, data.excerpt());
    assertEquals(AUTH_USER, data.author().username());
    assertEquals(MOCK_SLUG, data.slug());
  }

  @Test
  @Transactional(readOnly = true)
  @Order(1)
  public void testVerifyPostCreation() {
    postRepository
        .findOne(QPost.post.slug.eq(MOCK_SLUG))
        .ifPresentOrElse(
            post -> {
              assertEquals(TITLE, post.getTitle());
              assertEquals(EXCERPT, post.getExcerpt());
              assertEquals("Test blank", post.getPostContent());
              assertEquals(MOCK_SLUG, post.getSlug());
              assertEquals(AUTH_USER, post.getAuthor().getUsername());
              assertTrue(
                  TAGS.containsAll(post.getTags().stream().map(Tag::getDisplayName).toList()));
            },
            () -> fail("Post was not found"));
  }

  @SneakyThrows
  private MvcResult createPostRequest(PostCreationRequest postCreationRequest, String accessToken) {
    try (var mockNoDashUUID = Mockito.mockStatic(NoDashedUUIDGenerator.class)) {
      // Return a fixed UUID for testing
      mockNoDashUUID
          .when(() -> NoDashedUUIDGenerator.createNonDashedUUID(Mockito.any()))
          .thenReturn(MOCK_UUID);

      return getMockMvc()
          .perform(
              postWithEndpointAndPayload(EndpointConstant.ENDPOINT_POST, postCreationRequest)
                  .header(HttpHeaders.AUTHORIZATION, accessToken))
          .andExpect(status().isCreated())
          .andReturn();
    }
  }

  @DynamicPropertySource
  static void reinitializeJdbcUrl(DynamicPropertyRegistry registry) {
    reinitializeConnectionPropertiesInternal(registry);
  }
}
