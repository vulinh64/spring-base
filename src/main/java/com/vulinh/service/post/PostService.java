package com.vulinh.service.post;

import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.post.PostCreationDTO;
import com.vulinh.data.dto.post.PostDTO;
import com.vulinh.data.dto.post.SinglePostDTO;
import com.vulinh.data.mapper.PostMapper;
import com.vulinh.data.projection.PrefetchPostProjection;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.exception.CommonException;
import com.vulinh.service.post.create.PostCreationService;
import com.vulinh.service.post.edit.PostEditService;
import com.vulinh.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
public class PostService {

  private static final PostMapper POST_MAPPER = PostMapper.INSTANCE;

  private final PostRepository postRepository;

  private final PostCreationService postCreationService;
  private final PostEditService postEditService;
  private final PostRevisionService postRevisionService;

  public Page<PrefetchPostProjection> getPostsByCurrentUser(
      Pageable pageable, HttpServletRequest httpServletRequest) {
    return postRepository.findByAuthorId(
        SecurityUtils.getUserDTOOrThrow(httpServletRequest).id(), pageable);
  }

  public SinglePostDTO getSinglePost(String identity, HttpServletRequest httpServletRequest) {
    return postRepository
        .findSinglePost(identity, SecurityUtils.getUserDTOOrThrow(httpServletRequest).id())
        .map(POST_MAPPER::toSinglePostDTO)
        .orElseThrow(
            () ->
                new CommonException(
                    "Post with either id or slug [%s] not found".formatted(identity),
                    CommonMessage.MESSAGE_INVALID_ENTITY_ID,
                    null,
                    "Post"));
  }

  @Transactional
  public PostDTO createPost(
      PostCreationDTO postCreationDTO, HttpServletRequest httpServletRequest) {
    // Delegate to PostCreationService
    var entity = postCreationService.createPost(postCreationDTO, httpServletRequest);

    // Delegate to PostRevisionService
    postRevisionService.createPostCreationRevision(entity);

    return POST_MAPPER.toDto(entity);
  }

  @Transactional
  public boolean editPost(
      String postId, PostCreationDTO postCreationDTO, HttpServletRequest httpServletRequest) {
    return postEditService
        .editPost(postId, postCreationDTO, httpServletRequest)
        .map(
            post -> {
              postRevisionService.createPostEditRevision(post);
              return true;
            })
        .isPresent();
  }
}
