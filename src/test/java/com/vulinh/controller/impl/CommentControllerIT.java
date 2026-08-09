package com.vulinh.controller.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import module java.base;

import com.vulinh.AbstractBaseIntegration;
import com.vulinh.data.constant.EndpointConstant;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.request.NewCommentRequest;
import com.vulinh.data.dto.response.CommentResponse;
import com.vulinh.data.entity.RevisionType;
import com.vulinh.data.event.EventMessageWrapper;
import com.vulinh.data.event.EventType;
import com.vulinh.data.event.payload.NewCommentEvent;
import com.vulinh.utils.JsonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import tools.jackson.core.type.TypeReference;

class CommentControllerIT extends AbstractBaseIntegration {

  private static final UUID EXISTING_COMMENT_ID =
      UUID.fromString("c0a802df-9fe5-1c2a-819f-e5ec41120005");

  @Test
  @Sql(scripts = "/sql/seed-existing-post.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(
      scripts = "/sql/cleanup-existing-post.sql",
      executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
  void addCommentPersistsCommentRevisionAndEmitsEvent() throws Exception {
    var request = new NewCommentRequest("A comment created through `MockMvc`.");

    var queue =
        initializeRabbitMqQueue(
            applicationProperties.messageTopic().newComment(), "comment-controller-it-");

    try {
      var result =
          mockMvc
              .perform(
                  post(EndpointConstant.ENDPOINT_COMMENT + "/" + EXISTING_POST_ID)
                      .contentType(APPLICATION_JSON)
                      .content(JsonUtils.toMinimizedJSON(request))
                      .with(testUserJwt()))
              .andExpect(status().isCreated())
              .andExpect(jsonPath("$.data.postId").value(EXISTING_POST_ID.toString()))
              .andExpect(jsonPath("$.data.commentId").isNotEmpty())
              .andExpect(jsonPath("$.data.revisionNumber").isNumber())
              .andReturn();

      var response =
          JsonUtils.toObject(
              result.getResponse().getContentAsString(),
              new TypeReference<GenericResponse<CommentResponse>>() {});

      var commentId = response.data().commentId();

      assertEquals(
          request.content(),
          jdbcTemplate.queryForObject(
              "SELECT content FROM comment WHERE id = ?", String.class, commentId));
      assertEquals(
          EXISTING_POST_ID,
          jdbcTemplate.queryForObject(
              "SELECT post_id FROM comment WHERE id = ?", UUID.class, commentId));
      assertEquals(
          RevisionType.CREATED.ordinal(),
          jdbcTemplate.queryForObject(
              "SELECT revision_type FROM comment_revision WHERE comment_id = ?",
              Integer.class,
              commentId));

      var event = receiveEvent(queue, new TypeReference<EventMessageWrapper<NewCommentEvent>>() {});

      assertEquals(EventType.NEW_COMMENT, event.eventType());
      assertTestUserAction(event);
      assertEquals(EXISTING_POST_ID, event.data().postId());
      assertEquals("Original Post", event.data().title());
      assertEquals("Original excerpt", event.data().excerpt());
      assertEquals(commentId, event.data().commentId());
      assertEquals(request.content(), event.data().content());
    } finally {
      rabbitAdmin.deleteQueue(queue.getName());
    }
  }

  @Test
  @Sql(
      scripts = {"/sql/seed-existing-post.sql", "/sql/seed-existing-comment.sql"},
      executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(
      scripts = "/sql/cleanup-existing-post.sql",
      executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
  void editCommentUpdatesPersistedCommentAndCreatesRevision() throws Exception {
    var request = new NewCommentRequest("Updated comment content.");

    var result =
        mockMvc
            .perform(
                patch(EndpointConstant.ENDPOINT_COMMENT + "/" + EXISTING_COMMENT_ID)
                    .contentType(APPLICATION_JSON)
                    .content(JsonUtils.toMinimizedJSON(request))
                    .with(testUserJwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.postId").value(EXISTING_POST_ID.toString()))
            .andExpect(jsonPath("$.data.commentId").value(EXISTING_COMMENT_ID.toString()))
            .andExpect(jsonPath("$.data.revisionNumber").isNumber())
            .andReturn();

    var response =
        JsonUtils.toObject(
            result.getResponse().getContentAsString(),
            new TypeReference<GenericResponse<CommentResponse>>() {});

    assertEquals(EXISTING_POST_ID, response.data().postId());
    assertEquals(EXISTING_COMMENT_ID, response.data().commentId());
    assertEquals(
        request.content(),
        jdbcTemplate.queryForObject(
            "SELECT content FROM comment WHERE id = ?", String.class, EXISTING_COMMENT_ID));
    assertEquals(
        1,
        jdbcTemplate.queryForObject(
            "SELECT count(*) FROM comment_revision WHERE comment_id = ?",
            Integer.class,
            EXISTING_COMMENT_ID));
    assertEquals(
        RevisionType.UPDATED.ordinal(),
        jdbcTemplate.queryForObject(
            "SELECT revision_type FROM comment_revision WHERE comment_id = ?",
            Integer.class,
            EXISTING_COMMENT_ID));
  }
}
