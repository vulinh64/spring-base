package com.vulinh.it;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import module java.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vulinh.Constants;
import com.vulinh.data.constant.EndpointConstant;
import com.vulinh.data.dto.request.NewCommentRequest;
import com.vulinh.data.dto.request.PostCreationRequest;
import com.vulinh.data.dto.response.BasicPostResponse;
import com.vulinh.data.dto.response.CommentResponse;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.entity.Post;
import com.vulinh.data.entity.QPost;
import com.vulinh.data.entity.RevisionType;
import com.vulinh.data.entity.Tag;
import com.vulinh.data.entity.ids.CommentRevisionId;
import com.vulinh.data.repository.CommentRepository;
import com.vulinh.data.repository.CommentRevisionRepository;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.utils.JsonUtils;
import com.vulinh.utils.KeycloakInitializationUtils;
import com.vulinh.utils.post.NoDashedUUIDGenerator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.keycloak.admin.client.Keycloak;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
  static final String DANGEROUS_COMMENT_CONTENT =
      "<script>This is a comment</script>This is a comment";
  static final String SANITIZED_COMMENT_CONTENT = "This is a comment";
  static final String EDITED_COMMENT_CONTENT = "Edited Comment";

  // Shared across tests
  static UUID COMMENT_ID;
  static Long REVISION_NUMBER;
  static Long EDITED_REVISION_NUMBER;
  static UUID POST_ID;

  @Autowired PostRepository postRepository;
  @Autowired CommentRepository commentRepository;
  @Autowired CommentRevisionRepository commentRevisionRepository;

  @Autowired Keycloak keycloak;

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
  }

  @Test
  @Order(Integer.MIN_VALUE)
  void testKeycloakClient() {
    // Ugly hack to get it runs ONLY ONCE
    var clientId =
        KeycloakInitializationUtils.initializeKeycloak(
            getApplicationProperties().security(), KEYCLOAK_CONTAINER);

    assertNotNull(clientId);

    var realm = getApplicationProperties().security().realmName();

    assertDoesNotThrow(() -> keycloak.realm(realm).toRepresentation());

    var client =
        assertDoesNotThrow(() -> keycloak.realm(realm).clients().get(clientId).toRepresentation());

    assertEquals(clientId, client.getId());

    assertEquals(2, keycloak.realm(realm).users().count());
  }

  @Test
  @Transactional
  @Commit
  @Order(0)
  @SneakyThrows
  void testCreatePost() {
    var accessToken = getAccessToken(Constants.TEST_ADMIN);

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
    assertEquals(Constants.MOCK_SLUG, data.slug());
  }

  @Test
  @Transactional(readOnly = true)
  @Order(1)
  public void testVerifyPostCreation() {
    findCreatedPost()
        .ifPresentOrElse(
            post -> {
              assertEquals(TITLE, post.getTitle());
              assertEquals(EXCERPT, post.getExcerpt());
              assertEquals("Test blank", post.getPostContent());
              assertEquals(Constants.MOCK_SLUG, post.getSlug());
              assertTrue(
                  TAGS.containsAll(post.getTags().stream().map(Tag::getDisplayName).toList()));
              assertEquals(getUserId(Constants.TEST_ADMIN), post.getAuthorId());
            },
            () -> fail("Post was not found"));
  }

  @Test
  @Transactional
  @Commit
  @Order(2)
  @SneakyThrows
  void testCreateComment() {
    var postId = findCreatedPost().map(Post::getId).orElseGet(() -> fail("Post not found"));

    POST_ID = postId;

    var accessToken = getAccessToken(Constants.TEST_POWER_USER);

    var newCommentResult =
        getMockMvc()
            .perform(
                postWithEndpointAndPayload(
                        "%s/%s".formatted(EndpointConstant.ENDPOINT_COMMENT, postId),
                        NewCommentRequest.builder().content(DANGEROUS_COMMENT_CONTENT).build())
                    .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
            .andExpect(status().isCreated())
            .andReturn();

    var response =
        JsonUtils.toObject(
                newCommentResult.getResponse().getContentAsString(),
                new TypeReference<GenericResponse<CommentResponse>>() {})
            .data();

    COMMENT_ID = response.commentId();
    REVISION_NUMBER = response.revisionNumber();

    assertEquals(postId, response.postId());
  }

  private static @NotNull String bearerToken(String accessToken) {
    return "Bearer %s".formatted(accessToken);
  }

  @Test
  @Transactional(readOnly = true)
  @Order(3)
  void testVerifyComment() {
    assertNotNull(COMMENT_ID);
    assertNotNull(POST_ID);
    assertNotNull(REVISION_NUMBER);

    commentRepository
        .findById(COMMENT_ID)
        .ifPresentOrElse(
            comment -> {
              assertEquals(SANITIZED_COMMENT_CONTENT, comment.getContent());
              assertEquals(POST_ID, comment.getPostId());
              assertEquals(getUserId(Constants.TEST_POWER_USER), comment.getCreatedBy());
            },
            () -> fail("Comment not found"));

    commentRevisionRepository
        .findById(CommentRevisionId.of(COMMENT_ID, REVISION_NUMBER))
        .ifPresentOrElse(
            commentRevision -> {
              assertEquals(SANITIZED_COMMENT_CONTENT, commentRevision.getContent());
              assertEquals(POST_ID, commentRevision.getPostId());
              assertEquals(RevisionType.CREATED, commentRevision.getRevisionType());
            },
            () -> fail("Comment revision not found"));
  }

  @Test
  @Transactional
  @Commit
  @Order(4)
  @SneakyThrows
  void testEditComment() {
    var editCommentResponse =
        getMockMvc()
            .perform(
                patch(
                        "%s/%s".formatted(EndpointConstant.ENDPOINT_COMMENT, "{commentId}"),
                        COMMENT_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(
                        HttpHeaders.AUTHORIZATION,
                        bearerToken(getAccessToken(Constants.TEST_POWER_USER)))
                    .content(
                        JsonUtils.toMinimizedJSON(
                            NewCommentRequest.builder().content(EDITED_COMMENT_CONTENT).build())))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

    assertNotNull(editCommentResponse);

    var response =
        JsonUtils.toObject(
                editCommentResponse.getContentAsString(),
                new TypeReference<GenericResponse<CommentResponse>>() {})
            .data();

    assertEquals(COMMENT_ID, response.commentId());
    assertEquals(POST_ID, response.postId());

    var editedRevisionNumber = response.revisionNumber();

    assertNotEquals(REVISION_NUMBER, editedRevisionNumber);

    EDITED_REVISION_NUMBER = editedRevisionNumber;
  }

  @Test
  @Transactional(readOnly = true)
  @Order(5)
  void testVerifyEditedComment() {
    assertNotNull(EDITED_REVISION_NUMBER);

    commentRepository
        .findById(COMMENT_ID)
        .ifPresentOrElse(
            comment -> {
              assertEquals(EDITED_COMMENT_CONTENT, comment.getContent());
              assertEquals(POST_ID, comment.getPostId());
            },
            () -> fail("Edited comment not found"));

    commentRevisionRepository
        .findById(CommentRevisionId.of(COMMENT_ID, EDITED_REVISION_NUMBER))
        .ifPresentOrElse(
            commentRevision -> {
              assertEquals(EDITED_COMMENT_CONTENT, commentRevision.getContent());
              assertEquals(POST_ID, commentRevision.getPostId());
              assertEquals(RevisionType.UPDATED, commentRevision.getRevisionType());
              assertEquals(EDITED_REVISION_NUMBER, commentRevision.getId().getRevisionNumber());
            },
            () -> fail("Edited comment revision not found"));
  }

  @SneakyThrows
  private MvcResult createPostRequest(PostCreationRequest postCreationRequest, String accessToken) {
    try (var mockNoDashUUID = Mockito.mockStatic(NoDashedUUIDGenerator.class)) {
      // Return a fixed UUID for testing
      mockNoDashUUID
          .when(() -> NoDashedUUIDGenerator.createNonDashedUUID(any()))
          .thenReturn(Constants.MOCK_UUID);

      return getMockMvc()
          .perform(
              postWithEndpointAndPayload(EndpointConstant.ENDPOINT_POST, postCreationRequest)
                  .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken)))
          .andExpect(status().isCreated())
          .andReturn();
    }
  }

  private Optional<Post> findCreatedPost() {
    return postRepository.findOne(QPost.post.slug.eq(Constants.MOCK_SLUG));
  }

  private UUID getUserId(String user) {
    return UUID.fromString(
        keycloak
            .realm(getApplicationProperties().security().realmName())
            .users()
            .search(user)
            .getFirst()
            .getId());
  }
}
