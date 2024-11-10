package com.vulinh.service.post;

import com.vulinh.data.document.EPost.ESimplePost;
import com.vulinh.data.dto.post.PostCreationDTO;
import com.vulinh.data.dto.post.PostDTO;
import com.vulinh.data.dto.post.SinglePostDTO;
import com.vulinh.data.elasticsearch.EPostRepository;
import com.vulinh.data.entity.Post;
import com.vulinh.data.mapper.PostMapper;
import com.vulinh.data.projection.PrefetchPostProjection;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.factory.ElasticsearchEventFactory;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.service.post.create.PostCreationService;
import com.vulinh.service.post.edit.PostDeletionService;
import com.vulinh.service.post.edit.PostEditService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

  private static final PostMapper POST_MAPPER = PostMapper.INSTANCE;

  private static final ElasticsearchEventFactory ELASTICSEARCH_EVENT_FACTORY =
      ElasticsearchEventFactory.INSTANCE;

  private final PostRepository postRepository;
  private final EPostRepository ePostRepository;

  private final PostCreationService postCreationService;
  private final PostEditService postEditService;
  private final PostDeletionService postDeletionService;
  private final PostRevisionService postRevisionService;

  private final ApplicationEventPublisher applicationEventPublisher;

  public Page<PrefetchPostProjection> findPrefetchPosts(Pageable pageable) {
    return postRepository.findPrefetchPosts(pageable);
  }

  public SinglePostDTO getSinglePost(UUID postId) {
    return postRepository
        .findById(postId)
        .map(POST_MAPPER::toSinglePostDTO)
        .orElseThrow(() -> ExceptionFactory.INSTANCE.postNotFound(postId));
  }

  @Transactional
  public PostDTO createPost(
      PostCreationDTO postCreationDTO, HttpServletRequest httpServletRequest) {
    // Delegate to PostCreationService
    var entity = postCreationService.createPost(postCreationDTO, httpServletRequest);

    // Delegate to PostRevisionService
    postRevisionService.createPostCreationRevision(entity);

    publishPersistedElasticsearchPostDocument(entity);

    return POST_MAPPER.toDto(entity);
  }

  @Transactional
  public boolean editPost(
      UUID postId, PostCreationDTO postCreationDTO, HttpServletRequest httpServletRequest) {
    var possiblePost = postEditService.editPost(postId, postCreationDTO, httpServletRequest);

    if (possiblePost.isPresent()) {
      var post = possiblePost.get();

      postRevisionService.createPostEditRevision(post);

      publishPersistedElasticsearchPostDocument(post);

      return true;
    }

    return false;
  }

  @Transactional
  public boolean deletePost(UUID postId, HttpServletRequest httpServletRequest) {
    var possiblePost = postDeletionService.deletePost(postId, httpServletRequest);

    if (possiblePost.isPresent()) {
      var post = possiblePost.get();

      postRevisionService.createPostDeletionRevision(post);

      applicationEventPublisher.publishEvent(
          ELASTICSEARCH_EVENT_FACTORY.ofDeletion(POST_MAPPER.toDocumentedPost(post)));

      return true;
    }

    return false;
  }

  public Page<ESimplePost> quickSearch(String keyword, Pageable pageable) {
    return ePostRepository
        .findByTitleContainingIgnoreCaseOrPostContentContainingIgnoreCase(
            keyword, keyword, pageable)
        .map(ePost -> POST_MAPPER.toESimplePost(ePost, keyword));
  }

  private void publishPersistedElasticsearchPostDocument(Post post) {
    applicationEventPublisher.publishEvent(
        ELASTICSEARCH_EVENT_FACTORY.ofPersistence(POST_MAPPER.toDocumentedPost(post)));
  }
}
