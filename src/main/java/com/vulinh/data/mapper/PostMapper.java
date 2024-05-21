package com.vulinh.data.mapper;

import com.vulinh.data.dto.post.PostCreationDTO;
import com.vulinh.data.dto.post.PostDTO;
import com.vulinh.data.dto.post.SinglePostDTO;
import com.vulinh.data.entity.*;
import java.util.Collection;
import java.util.stream.Collectors;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(
    builder = @Builder(disableBuilder = true),
    imports = {Tag.class, Collectors.class, PostRevisionId.class})
public interface PostMapper extends EntityDTOMapper<Post, PostDTO> {

  PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

  SinglePostDTO toSinglePostDTO(Post post);

  @Override
  PostDTO toDto(Post entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "updatedDate", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "tags", source = "tags")
  Post toEntity(PostCreationDTO dto, Users author, Category category, Collection<Tag> tags);

  @Mapping(
      target = "id",
      expression = "java(PostRevisionId.builder().postId(post.getId()).build())")
  @Mapping(
      target = "tags",
      expression =
          "java(post.getTags().stream().map(Tag::getDisplayName).collect(Collectors.joining(\",\")))")
  @Mapping(target = "authorId", source = "post.author.id")
  @Mapping(target = "categoryId", source = "post.category.id")
  @Mapping(target = "revisionCreatedDate", ignore = true)
  @Mapping(target = "revisionCreatedBy", ignore = true)
  PostRevision toPostRevision(Post post, RevisionType revisionType);

  @Mapping(target = "author", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "updatedDate", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "tags", source = "tags")
  @Mapping(target = "category", source = "category")
  void merge(
      PostCreationDTO postCreationDTO,
      Category category,
      Collection<Tag> tags,
      @MappingTarget Post post);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "updatedDate", ignore = true)
  @Mapping(target = "tags", source = "tags")
  @Mapping(target = "author", ignore = true)
  @Mapping(
      target = "category",
      source = "category",
      nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Post applyRevision(
      PostRevision postRevision, Category category, Collection<Tag> tags, @MappingTarget Post post);
}
