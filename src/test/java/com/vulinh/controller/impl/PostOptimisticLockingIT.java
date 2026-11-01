package com.vulinh.controller.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import module java.base;

import com.vulinh.AbstractBaseIntegration;
import com.vulinh.data.dto.request.PostEditRequest;
import com.vulinh.data.entity.Post;
import com.vulinh.utils.JsonUtils;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.ResultActions;

@Sql(scripts = "/sql/seed-existing-post.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup-existing-post.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class PostOptimisticLockingIT extends AbstractBaseIntegration {

  @Autowired EntityManagerFactory entityManagerFactory;

  @Test
  void twoEditorsMustResolveAConflictBeforeSavingAgain() throws Exception {
    mockMvc
        .perform(get("/post/" + EXISTING_POST_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.version").value(0));

    save(original().withTitle("First editor")).andExpect(status().isOk());
    save(original().withTitle("Second editor"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorCode").value("M1005"));
    assertEquals("First Editor", title());
    assertEquals(1L, version());
    assertEquals(1, revisionCount());

    // A stale request is a conflict even if its content matches the latest post.
    save(original().withTitle("First editor"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorCode").value("M1005"));

    mockMvc
        .perform(get("/post/" + EXISTING_POST_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.version").value(1));
    save(original().withTitle("Resolved draft").withVersion(1L)).andExpect(status().isOk());
    assertEquals(2L, version());
    assertEquals(2, revisionCount());
  }

  @Test
  void missingAndNegativeVersionsAreRejectedWithoutWrites() throws Exception {
    for (Long invalid : Arrays.asList(null, -1L)) {
      save(original().withVersion(invalid))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errorCode").value("M1006"));
    }
    assertEquals(0L, version());
    assertEquals(0, revisionCount());
  }

  @Test
  void unchangedSaveDoesNotAdvanceVersionOrCreateRevision() throws Exception {
    save(original()).andExpect(status().isNoContent());
    assertEquals(0L, version());
    assertEquals(0, revisionCount());
  }

  @Test
  void slugOnlySaveAdvancesVersion() throws Exception {
    save(original().withSlug("changed-slug")).andExpect(status().isOk());
    assertEquals(1L, version());
    save(original()).andExpect(status().isConflict());
  }

  @Test
  void tagOnlySaveAdvancesVersionAndRejectsStaleEditor() throws Exception {
    save(original().withTags(Set.of())).andExpect(status().isOk());
    assertEquals(1L, version());
    save(original().withTitle("Stale editor")).andExpect(status().isConflict());
    assertEquals(
        0,
        jdbcTemplate.queryForObject(
            "SELECT count(*) FROM post_tag_mapping WHERE post_id = ?",
            Integer.class,
            EXISTING_POST_ID));
    assertEquals(1, revisionCount());
  }

  @Test
  void categoryOnlySaveAdvancesVersionAndRejectsStaleEditor() throws Exception {
    var categoryId = UUID.randomUUID();
    jdbcTemplate.update(
        "INSERT INTO category(id, category_slug, display_name) VALUES (?, ?, ?)",
        categoryId,
        "locking-test",
        "Locking test");
    try {
      save(original().withCategoryId(categoryId)).andExpect(status().isOk());
      assertEquals(1L, version());
      save(original().withTitle("Stale editor")).andExpect(status().isConflict());
      assertEquals(
          categoryId,
          jdbcTemplate.queryForObject(
              "SELECT category_id FROM post WHERE id = ?", UUID.class, EXISTING_POST_ID));
    } finally {
      jdbcTemplate.update(
          "UPDATE post SET category_id = ? WHERE id = ?", new UUID(0, 0), EXISTING_POST_ID);
      jdbcTemplate.update("DELETE FROM category WHERE id = ?", categoryId);
    }
  }

  @Test
  void atomicVersionCheckRejectsWritersThatLoadedTheSameVersion() {
    // Both transactions read version 0 before either writes; the loser must roll back.
    try (var first = entityManagerFactory.createEntityManager();
        var second = entityManagerFactory.createEntityManager()) {
      first.getTransaction().begin();
      second.getTransaction().begin();
      try {
        var winner = first.find(Post.class, EXISTING_POST_ID);
        var loser = second.find(Post.class, EXISTING_POST_ID);
        assertEquals(1, loser.getTags().size());
        winner.setTitle("Winning concurrent writer");
        first.getTransaction().commit();

        loser.getTags().clear();
        assertThrows(OptimisticLockException.class, second::flush);
        second.getTransaction().rollback();
      } finally {
        if (first.getTransaction().isActive()) first.getTransaction().rollback();
        if (second.getTransaction().isActive()) second.getTransaction().rollback();
      }
    }
    assertEquals("Winning concurrent writer", title());
    assertEquals(1L, version());
    assertEquals(
        1,
        jdbcTemplate.queryForObject(
            "SELECT count(*) FROM post_tag_mapping WHERE post_id = ?",
            Integer.class,
            EXISTING_POST_ID));
  }

  private PostEditRequest original() {
    return PostEditRequest.builder()
        .title("Original Post")
        .excerpt("Original excerpt")
        .postContent("Original content")
        .slug("original-post")
        .tags(Set.of("existing tag"))
        .version(0L)
        .build();
  }

  private ResultActions save(PostEditRequest request) throws Exception {
    return mockMvc.perform(
        patch("/post/" + EXISTING_POST_ID)
            .contentType(APPLICATION_JSON)
            .content(JsonUtils.toMinimizedJSON(request))
            .with(testUserJwt()));
  }

  private Long version() {
    return jdbcTemplate.queryForObject(
        "SELECT version FROM post WHERE id = ?", Long.class, EXISTING_POST_ID);
  }

  private String title() {
    return jdbcTemplate.queryForObject(
        "SELECT title FROM post WHERE id = ?", String.class, EXISTING_POST_ID);
  }

  private int revisionCount() {
    return Objects.requireNonNull(
        jdbcTemplate.queryForObject(
            "SELECT count(*) FROM post_revision WHERE post_id = ?",
            Integer.class,
            EXISTING_POST_ID));
  }
}
