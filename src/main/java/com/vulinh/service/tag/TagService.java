package com.vulinh.service.tag;

import module java.base;

import com.google.common.collect.ImmutableSet;
import com.vulinh.data.dto.request.PostCreationRequest;
import com.vulinh.data.entity.Tag;
import com.vulinh.data.repository.TagRepository;
import com.vulinh.factory.PostFactory;
import com.vulinh.utils.post.PostUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

  private final TagRepository tagRepository;

  @NonNull
  public Set<Tag> parseTags(Collection<String> rawTags) {
    if (rawTags.isEmpty()) {
      return Collections.emptySet();
    }

    var actualTags =
        rawTags.stream()
            .filter(StringUtils::isNotBlank)
            .map(PostUtils::normalizeText)
            .collect(Collectors.toSet());

    var resultBuilder = ImmutableSet.<Tag>builder();

    var existingTags = tagRepository.findByDisplayNameInIgnoreCase(actualTags);

    if (!existingTags.isEmpty()) {
      resultBuilder.addAll(existingTags);
    }

    var nonMatchingTags =
        actualTags.stream()
            .filter(
                rawTag ->
                    // Tags that did not present within database
                    existingTags.isEmpty()
                        || existingTags.stream()
                            .map(Tag::getDisplayName)
                            .noneMatch(tag -> tag.equalsIgnoreCase(rawTag)))
            .map(PostFactory.INSTANCE::createTag)
            .toList();

    resultBuilder.addAll(tagRepository.saveAll(nonMatchingTags));

    return resultBuilder.build();
  }

  public Set<Tag> parseTags(String rawTagsWithDelimiter) {
    return parseTags(Arrays.asList(rawTagsWithDelimiter.split(",")));
  }

  public Set<Tag> parseTags(PostCreationRequest postCreationRequest) {
    return parseTags(postCreationRequest.tags());
  }
}
