package com.vulinh.service.tag;

import com.google.common.collect.ImmutableSet;
import com.vulinh.data.dto.post.PostCreationDTO;
import com.vulinh.data.entity.Tag;
import com.vulinh.data.repository.TagRepository;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

  private final TagRepository tagRepository;

  public Collection<Tag> parseTags(PostCreationDTO postCreationDTO) {
    var rawTags = postCreationDTO.tags();

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
}
