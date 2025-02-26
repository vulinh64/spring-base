package com.vulinh.data.mapper;

import com.vulinh.data.dto.comment.NewCommentReplyDTO;
import com.vulinh.data.entity.Comment;
import com.vulinh.data.entity.CommentRevision;
import com.vulinh.data.entity.RevisionType;
import com.vulinh.data.entity.Users;
import com.vulinh.data.entity.ids.CommentRevisionId;
import java.util.UUID;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(builder = @Builder(disableBuilder = true))
public interface CommentMapper {

  CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

  @Mapping(target = "updatedDate", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "withUpdatedDate", ignore = true)
  @Mapping(target = "withPostId", ignore = true)
  @Mapping(target = "withId", ignore = true)
  @Mapping(target = "withCreatedDate", ignore = true)
  @Mapping(target = "withCreatedBy", ignore = true)
  @Mapping(target = "withContent", ignore = true)
  Comment fromNewCommentReply(NewCommentReplyDTO newComment, Users createdBy, UUID postId);

  @Mapping(target = "id", expression = "java(createTransientId(comment))")
  @Mapping(target = "revisionCreatedBy", ignore = true)
  @Mapping(target = "revisionCreatedDate", ignore = true)
  @Mapping(target = "withRevisionType", ignore = true)
  @Mapping(target = "withRevisionCreatedDate", ignore = true)
  @Mapping(target = "withRevisionCreatedBy", ignore = true)
  @Mapping(target = "withPostId", ignore = true)
  @Mapping(target = "withId", ignore = true)
  @Mapping(target = "withContent", ignore = true)
  CommentRevision fromComment(Comment comment, RevisionType revisionType);

  default CommentRevisionId createTransientId(Comment comment) {
    return CommentRevisionId.builder().commentId(comment.getId()).build();
  }
}
