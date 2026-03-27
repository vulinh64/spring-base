package com.vulinh.service.post;

import module java.base;

import com.vulinh.data.dto.projection.PostSearchProjection;
import com.vulinh.data.dto.projection.PrefetchPostProjection;
import com.vulinh.data.dto.request.PostCreationRequest;
import com.vulinh.data.dto.response.*;
import com.vulinh.data.entity.Author;
import com.vulinh.data.mapper.PostMapper;
import com.vulinh.data.repository.AuthorRepository;
import com.vulinh.data.repository.CommentRepository;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.service.author.AuthorService;
import com.vulinh.service.event.EventService;
import com.vulinh.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

  static final PostMapper POST_MAPPER = PostMapper.INSTANCE;

  final PostRepository postRepository;
  final AuthorRepository authorRepository;
  final CommentRepository commentRepository;

  final AuthorService authorService;
  final PostValidationService postValidationService;
  final PostCreationService postCreationService;
  final PostEditService postEditService;
  final PostDeletionService postDeletionService;
  final PostRevisionService postRevisionService;
  final EventService eventService;

  public Page<PrefetchPostResponse> findPrefetchPosts(Pageable pageable) {
    var page = postRepository.findPrefetchPosts(pageable);
    return enrichWithAuthors(page);
  }

  public Page<PrefetchPostResponse> findPostsByCategory(String categorySlug, Pageable pageable) {
    var page = postRepository.findPostsByCategorySlug(categorySlug, pageable);
    return enrichWithAuthors(page);
  }

  public Page<PrefetchPostResponse> searchPosts(String query, Pageable pageable) {
    var page = postRepository.searchPosts(query, pageable);
    return enrichSearchResults(page);
  }

  public SinglePostResponse getSinglePost(String identity) {
    var post = postValidationService.getPostByIdentity(identity);

    var authorResponse =
        authorRepository
            .findById(post.getAuthorId())
            .map(PostService::toAuthorResponse)
            .orElse(null);

    var commentCount = commentRepository.countByPostId(post.getId());

    return POST_MAPPER.toSinglePostDTO(post, authorResponse).withCommentCount(commentCount);
  }

  @Transactional
  public BasicPostResponse createPost(PostCreationRequest postCreationRequest) {
    // Delegate to PostCreationService
    var newPost = postCreationService.createPost(postCreationRequest);

    // Delegate to PostRevisionService
    var revisionNumber = postRevisionService.createPostCreationRevision(newPost);

    // Delegate to EventService
    eventService.sendNewPostEvent(newPost, SecurityUtils.getUserDTOOrThrow());

    return POST_MAPPER.toDto(newPost).withRevisionNumber(revisionNumber);
  }

  @Transactional
  public boolean editPost(UUID postId, PostCreationRequest postCreationRequest) {
    var possiblePost = postEditService.editPost(postId, postCreationRequest);

    if (possiblePost.isPresent()) {
      var post = possiblePost.get();

      postRevisionService.createPostEditRevision(post);

      return true;
    }

    return false;
  }

  @Transactional
  public boolean deletePost(UUID postId) {
    var possiblePost = postDeletionService.deletePost(postId);

    if (possiblePost.isPresent()) {
      var post = possiblePost.get();

      postRevisionService.createPostDeletionRevision(post);

      return true;
    }

    return false;
  }

  // --- Author enrichment helpers ---

  private Page<PrefetchPostResponse> enrichSearchResults(Page<PostSearchProjection> page) {
    var authorIds =
        page.getContent().stream()
            .map(PostSearchProjection::getAuthorId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

    var authorMap = authorService.findAuthorMapByIds(authorIds);

    return page.map(
        projection ->
            PrefetchPostResponse.builder()
                .id(projection.getId())
                .title(projection.getTitle())
                .excerpt(projection.getExcerpt())
                .slug(projection.getSlug())
                .createdDateTime(projection.getCreatedDateTime())
                .updatedDateTime(projection.getUpdatedDateTime())
                .authorId(projection.getAuthorId())
                .author(
                    Optional.ofNullable(authorMap.get(projection.getAuthorId()))
                        .map(PostService::toAuthorResponse)
                        .orElse(null))
                .category(
                    projection.getCategoryId() != null
                        ? CategoryResponse.builder()
                            .id(projection.getCategoryId())
                            .categorySlug(projection.getCategorySlug())
                            .displayName(projection.getDisplayName())
                            .build()
                        : null)
                .build());
  }

  private Page<PrefetchPostResponse> enrichWithAuthors(Page<PrefetchPostProjection> page) {
    var authorIds =
        page.getContent().stream()
            .map(PrefetchPostProjection::getAuthorId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

    var authorMap = authorService.findAuthorMapByIds(authorIds);

    return page.map(
        projection -> {
          var category = projection.getCategory();

          return PrefetchPostResponse.builder()
              .id(projection.getId())
              .title(projection.getTitle())
              .excerpt(projection.getExcerpt())
              .slug(projection.getSlug())
              .createdDateTime(projection.getCreatedDateTime())
              .updatedDateTime(projection.getUpdatedDateTime())
              .authorId(projection.getAuthorId())
              .author(
                  Optional.ofNullable(authorMap.get(projection.getAuthorId()))
                      .map(PostService::toAuthorResponse)
                      .orElse(null))
              .category(
                  category != null
                      ? CategoryResponse.builder()
                          .id(category.getId())
                          .categorySlug(category.getCategorySlug())
                          .displayName(category.getDisplayName())
                          .build()
                      : null)
              .build();
        });
  }

  static AuthorResponse toAuthorResponse(Author author) {
    return AuthorResponse.builder()
        .id(author.getId())
        .username(author.getUsername())
        .email(author.getEmail())
        .build();
  }
}
