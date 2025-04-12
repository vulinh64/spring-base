package com.vulinh.data.mapper;

import com.vulinh.data.base.EntityDTOMapper;
import com.vulinh.data.document.EPost;
import com.vulinh.data.dto.projection.PostRevisionProjection;
import com.vulinh.data.dto.request.PostCreationRequest;
import com.vulinh.data.dto.response.BasicPostResponse;
import com.vulinh.data.dto.response.ESimplePostResponse;
import com.vulinh.data.dto.response.PostRevisionResponse;
import com.vulinh.data.dto.response.SinglePostResponse;
import com.vulinh.data.entity.*;
import com.vulinh.data.entity.ids.PostRevisionId;
import java.util.Collection;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(
    builder = @Builder(disableBuilder = true),
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    imports = {Tag.class, Collectors.class, PostRevisionId.class, StringUtils.class})
public interface PostMapper extends EntityDTOMapper<Post, BasicPostResponse>, DateTimeMappable {

  PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

  SinglePostResponse toSinglePostDTO(Post post);

  @Override
  BasicPostResponse toDto(Post entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "updatedDate", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "tags", source = "tags")
  @Mapping(target = "comments", ignore = true)
  Post toEntity(PostCreationRequest dto, Users author, Category category, Collection<Tag> tags);

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
  @Mapping(target = "comments", ignore = true)
  void merge(
      PostCreationRequest postCreationRequest,
      Category category,
      Collection<Tag> tags,
      @MappingTarget Post post);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "updatedDate", ignore = true)
  @Mapping(target = "author", ignore = true)
  @Mapping(target = "comments", ignore = true)
  @Mapping(target = "tags", source = "tags")
  @Mapping(
      target = "category",
      source = "category",
      nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Post applyRevision(
      PostRevision postRevision, Category category, Collection<Tag> tags, @MappingTarget Post post);

  EPost toDocumentedPost(Post post);

  @Mapping(
      target = "shortContent",
      expression =
          """
          java(
            StringUtils.abbreviate(
              post.postContent(),
              StringUtils.normalizeSpace(post.postContent())
                .toLowerCase()
                .indexOf(keyword.toLowerCase()),
              50)
          )
          """)
  ESimplePostResponse toESimplePost(EPost post, String keyword);

  PostRevisionResponse toPostRevisionResponse(PostRevisionProjection postRevisionProjection);
}
