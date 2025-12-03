package com.vulinh.data.mapper;

import module java.base;

import com.vulinh.data.dto.request.NewCommentRequest;
import com.vulinh.data.entity.Comment;
import com.vulinh.data.entity.CommentRevision;
import com.vulinh.data.entity.Post;
import com.vulinh.data.entity.RevisionType;
import com.vulinh.data.entity.ids.CommentRevisionId;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(builder = @Builder(disableBuilder = true), unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

  CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

  @Mapping(target = "updatedDate", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "postId", source = "post.id")
  Comment fromNewComment(NewCommentRequest newComment, Post post);

  @Mapping(target = "id", expression = "java(createTransientId(comment))")
  @Mapping(target = "revisionCreatedBy", ignore = true)
  @Mapping(target = "revisionCreatedDate", ignore = true)
  CommentRevision fromComment(Comment comment, RevisionType revisionType);

  default CommentRevisionId createTransientId(Comment comment) {
    return CommentRevisionId.builder().commentId(comment.getId()).build();
  }
}
