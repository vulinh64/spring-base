package com.vulinh.service.tag;

import com.google.common.collect.ImmutableSet;
import com.vulinh.data.dto.post.PostCreationDTO;
import com.vulinh.data.entity.Tag;
import com.vulinh.data.repository.TagRepository;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

  private final TagRepository tagRepository;

  public Set<Tag> parseTags(Collection<String> rawTags) {
    var resultBuilder = ImmutableSet.<Tag>builder();

    var existingTags = tagRepository.findByDisplayNameInIgnoreCase(rawTags);

    if (!existingTags.isEmpty()) {
      resultBuilder.addAll(existingTags);
    }

    var nonMatchingTags =
        rawTags.stream()
            .filter(
                rawTag ->
                    // Tags that did not present within database
                    existingTags.isEmpty()
                        || existingTags.stream()
                            .map(Tag::getDisplayName)
                            .noneMatch(tag -> tag.equalsIgnoreCase(rawTag)))
            .map(Tag::of)
            .toList();

    resultBuilder.addAll(tagRepository.saveAll(nonMatchingTags));

    return resultBuilder.build();
  }

  public Set<Tag> parseTags(String rawTagsWithDelimiter) {
    return parseTags(Arrays.asList(rawTagsWithDelimiter.split(",")));
  }

  public Set<Tag> parseTags(PostCreationDTO postCreationDTO) {
    return parseTags(postCreationDTO.tags());
  }
}
