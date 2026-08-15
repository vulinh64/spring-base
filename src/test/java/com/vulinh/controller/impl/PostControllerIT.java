package com.vulinh.controller.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import module java.base;

import com.vulinh.AbstractBaseIntegration;
import com.vulinh.data.constant.EndpointConstant;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.request.PostCreationRequest;
import com.vulinh.data.dto.response.BasicPostResponse;
import com.vulinh.data.entity.RevisionType;
import com.vulinh.data.entity.ids.PostRevisionId;
import com.vulinh.data.event.EventMessageWrapper;
import com.vulinh.data.event.EventType;
import com.vulinh.data.event.payload.NewPostEvent;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.data.repository.PostRevisionRepository;
import com.vulinh.utils.JsonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import tools.jackson.core.type.TypeReference;

class PostControllerIT extends AbstractBaseIntegration {

  @Autowired private PostRepository postRepository;

  @Autowired private PostRevisionRepository postRevisionRepository;

  @Test
  @Sql(
      statements = {
        "DELETE FROM post_revision WHERE post_id IN (SELECT id FROM post WHERE slug = 'integration-test-post');",
        "DELETE FROM post_tag_mapping WHERE post_id IN (SELECT id FROM post WHERE slug = 'integration-test-post');",
        "DELETE FROM post WHERE slug = 'integration-test-post';",
        """
        DELETE FROM tag
        WHERE display_name IN ('spring boot', 'integration testing')
        AND NOT EXISTS (SELECT 1 FROM post_tag_mapping WHERE post_tag_mapping.tag_id = tag.id);
        """,
        "ALTER SEQUENCE post_revision_seq RESTART WITH 2;"
      },
      executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
  void createPostPersistsPostAndInitialRevisionForAuthenticatedUser() throws Exception {
    var request =
        PostCreationRequest.builder()
            .title("Integration Test Post")
            .excerpt("A post created through MockMvc")
            .postContent("# Integration test\n\nThis post is persisted to PostgreSQL.")
            .slug("integration-test-post")
            .tags(Set.of("Spring Boot", "Integration Testing"))
            .build();

    var queue =
        initializeRabbitMqQueue(
            applicationProperties.messageTopic().newPost(), "post-controller-it-");

    try {
      testNewPostInternal(request, queue);
    } finally {
      rabbitAdmin.deleteQueue(queue.getName());
    }
  }

  @Test
  @Sql(scripts = "/sql/seed-existing-post.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(
      scripts = "/sql/cleanup-existing-post.sql",
      executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
  void editPostUpdatesPersistedPostAndCreatesRevision() throws Exception {
    var request =
        PostCreationRequest.builder()
            .title("Updated Existing Post")
            .excerpt("Updated excerpt")
            .postContent("Updated content")
            .slug("updated-existing-post")
            .tags(Set.of("existing tag"))
            .build();

    mockMvc
        .perform(
            patch(EndpointConstant.ENDPOINT_POST + "/" + EXISTING_POST_ID)
                .contentType(APPLICATION_JSON)
                .content(JsonUtils.toMinimizedJSON(request))
                .with(testUserJwt()))
        .andExpect(status().isOk());

    assertEquals(
        "Updated Existing Post",
        jdbcTemplate.queryForObject(
            "SELECT title FROM post WHERE id = ?", String.class, EXISTING_POST_ID));
    assertEquals(
        "updated-existing-post",
        jdbcTemplate.queryForObject(
            "SELECT slug FROM post WHERE id = ?", String.class, EXISTING_POST_ID));
    assertEquals(
        "Updated excerpt",
        jdbcTemplate.queryForObject(
            "SELECT excerpt FROM post WHERE id = ?", String.class, EXISTING_POST_ID));
    assertEquals(
        "Updated content",
        jdbcTemplate.queryForObject(
            "SELECT post_content FROM post WHERE id = ?", String.class, EXISTING_POST_ID));
    assertEquals(
        1,
        jdbcTemplate.queryForObject(
            "SELECT count(*) FROM post_revision WHERE post_id = ?",
            Integer.class,
            EXISTING_POST_ID));
    assertEquals(
        RevisionType.UPDATED.ordinal(),
        jdbcTemplate.queryForObject(
            "SELECT revision_type FROM post_revision WHERE post_id = ?",
            Integer.class,
            EXISTING_POST_ID));
  }

  private void testNewPostInternal(PostCreationRequest request, Queue queue) throws Exception {
    var result =
        mockMvc
            .perform(
                post(EndpointConstant.ENDPOINT_POST)
                    .contentType(APPLICATION_JSON)
                    .content(JsonUtils.toMinimizedJSON(request))
                    .with(testUserJwt()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.id").isNotEmpty())
            .andExpect(jsonPath("$.data.title").value("Integration Test Post"))
            .andExpect(jsonPath("$.data.slug").value("integration-test-post"))
            .andExpect(jsonPath("$.data.authorId").value(USER_ID.toString()))
            .andExpect(jsonPath("$.data.revisionNumber").isNumber())
            .andExpect(jsonPath("$.data.tags.length()").value(2))
            .andReturn();

    var response =
        JsonUtils.toObject(
            result.getResponse().getContentAsString(),
            new TypeReference<GenericResponse<BasicPostResponse>>() {});

    var postId = response.data().id();
    var revisionNumber = response.data().revisionNumber();

    var persistedPost = postRepository.findById(postId);
    var persistedRevision =
        postRevisionRepository.findById(PostRevisionId.of(postId, revisionNumber));

    assertTrue(persistedPost.isPresent());
    assertEquals(USER_ID, persistedPost.orElseThrow().getAuthorId());
    assertEquals("Integration Test Post", persistedPost.orElseThrow().getTitle());
    assertEquals("integration-test-post", persistedPost.orElseThrow().getSlug());
    assertTrue(persistedRevision.isPresent());
    assertEquals(RevisionType.CREATED, persistedRevision.orElseThrow().getRevisionType());

    var event = receiveEvent(queue, new TypeReference<EventMessageWrapper<NewPostEvent>>() {});

    assertEquals(EventType.NEW_POST, event.eventType());
    assertTestUserAction(event);
    assertEquals(postId, event.data().postId());
    assertEquals(request.title(), event.data().title());
    assertEquals(request.excerpt(), event.data().excerpt());
  }
}
