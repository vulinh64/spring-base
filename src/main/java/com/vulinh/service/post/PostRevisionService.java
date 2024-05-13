package com.vulinh.service.post;

import com.vulinh.data.entity.Post;
import com.vulinh.data.entity.RevisionType;
import com.vulinh.data.mapper.PostMapper;
import com.vulinh.data.repository.PostRevisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostRevisionService {

  private final PostRevisionRepository postRevisionRepository;

  @Transactional
  public void createPostCreationRevision(Post post) {
    createRevisionInternal(post, RevisionType.CREATED);
  }

  @Transactional
  public void createPostEditRevision(Post post) {
    createRevisionInternal(post, RevisionType.UPDATED);
  }

  private void createRevisionInternal(Post post, RevisionType revisionType) {
    postRevisionRepository.save(PostMapper.INSTANCE.toPostRevision(post, revisionType));
  }
}
