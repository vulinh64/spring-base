package com.vulinh.it;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import module java.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vulinh.data.constant.EndpointConstant;
import com.vulinh.data.dto.request.PostCreationRequest;
import com.vulinh.data.dto.response.BasicPostResponse;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.entity.Tag;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.utils.JsonUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
class PostControllerIT extends IntegrationTestBase {

  static final Set<String> TAGS = Set.of("integration test");

  static final String TITLE = "Test Title";
  static final String EXCERPT = "Test Excerpt";
  static final String SLUG = "test-slug";

  @Autowired PostRepository postRepository;

  @Test
  @Transactional
  @SneakyThrows
  void testCreatePost() {
    var postCreationRequest =
        PostCreationRequest.builder()
            .title(TITLE)
            .excerpt(EXCERPT)
            .postContent("<script>Test Content</script>Test blank")
            .slug(SLUG)
            .tags(TAGS)
            .build();

    var accessToken = getAdminAccessToken();

    var postCreationResult =
        getMockMvc()
            .perform(
                postWithEndpointAndPayload(EndpointConstant.ENDPOINT_POST, postCreationRequest)
                    .header("Authorization", accessToken))
            .andExpect(status().isCreated())
            .andReturn();

    var response =
        JsonUtils.toObject(
            postCreationResult.getResponse().getContentAsString(),
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

  @DynamicPropertySource
  static void reinitializeJdbcUrl(DynamicPropertyRegistry registry) {
    reinitializeConnectionPropertiesInternal(registry);
  }
}
