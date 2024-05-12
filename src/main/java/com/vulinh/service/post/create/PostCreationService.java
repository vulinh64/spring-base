package com.vulinh.service.post.create;

import com.google.common.collect.ImmutableSet;
import com.vulinh.constant.CommonConstant;
import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.post.PostCreationDTO;
import com.vulinh.data.dto.user.UserBasicDTO;
import com.vulinh.data.entity.Post;
import com.vulinh.data.entity.Tag;
import com.vulinh.data.mapper.PostMapper;
import com.vulinh.data.repository.CategoryRepository;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.data.repository.TagRepository;
import com.vulinh.data.repository.UserRepository;
import com.vulinh.exception.CommonException;
import com.vulinh.exception.ExceptionBuilder;
import com.vulinh.utils.PostUtils;
import com.vulinh.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostCreationService {

  private static final PostMapper POST_MAPPER = PostMapper.INSTANCE;

  private final PostCreationValidationService postCreationValidationService;

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final TagRepository tagRepository;

  @Transactional
  public Post createPost(PostCreationDTO postCreationDTO, HttpServletRequest httpServletRequest) {
    postCreationValidationService.validatePost(postCreationDTO);

    var actualCreationDTO = getActualCreationDTO(postCreationDTO);

    var author =
        SecurityUtils.getUserDTO(httpServletRequest)
            .map(UserBasicDTO::id)
            .flatMap(userRepository::findById)
            .orElseThrow(ExceptionBuilder::invalidAuthorization);

    var categoryId = postCreationDTO.categoryId();

    var category =
        Optional.ofNullable(categoryId)
            .or(() -> Optional.of(CommonConstant.UNCATEGORIZED_ID))
            .flatMap(categoryRepository::findById)
            .orElseThrow(
                () ->
                    new CommonException(
                        "Invalid provided category ID: %s, or default category [%s] did not exist"
                            .formatted(categoryId, CommonConstant.UNCATEGORIZED_ID),
                        CommonMessage.MESSAGE_POST_INVALID_CATEGORY));

    var tags = parseTags(postCreationDTO);

    return postRepository.save(POST_MAPPER.toEntity(actualCreationDTO, author, category, tags));
  }

  private Collection<Tag> parseTags(PostCreationDTO postCreationDTO) {
    var rawTags =
        postCreationDTO.tags().stream().map(String::toLowerCase).collect(Collectors.toSet());

    var resultBuilder = ImmutableSet.<Tag>builder();

    var existingTags = tagRepository.findByDisplayNameInIgnoreCase(rawTags);

    if (!existingTags.isEmpty()) {
      resultBuilder.addAll(existingTags);
    }

    // Don't use Set here, the tags have null ids
    // and by equals/hashCode contract, they will be equal
    // If you want to use Set, you'll have to wrap the object
    // by using Guava's Equivalence equality
    var nonMatchingTags =
        rawTags.stream()
            .filter(
                rawTag ->
                    existingTags.isEmpty()
                        || existingTags.stream()
                            .map(Tag::getDisplayName)
                            .noneMatch(tag -> tag.equalsIgnoreCase(rawTag)))
            .map(Tag::of)
            .toList();

    resultBuilder.addAll(tagRepository.saveAll(nonMatchingTags));

    return resultBuilder.build();
  }

  private static PostCreationDTO getActualCreationDTO(PostCreationDTO postCreationDTO) {
    var slug = postCreationDTO.slug();

    return postCreationDTO
        .withSlug(
            StringUtils.isBlank(slug)
                ? PostUtils.createPostSlug(postCreationDTO.title())
                : PostUtils.createPostSlug(slug))
        .withTags(
            postCreationDTO.tags().stream()
                .filter(StringUtils::isNotBlank)
                // Convert all tags to lower case
                .map(String::toLowerCase)
                // Remove extra space
                .map(StringUtils::normalizeSpace)
                .collect(Collectors.toSet()));
  }
}
