package com.vulinh.data.mapper;

import com.vulinh.data.dto.comment.NewCommentDTO;
import com.vulinh.data.entity.Comment;
import com.vulinh.data.entity.CommentRevision;
import com.vulinh.data.entity.RevisionType;
import com.vulinh.factory.CommentFactory;
import java.util.UUID;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(builder = @Builder(disableBuilder = true), imports = CommentFactory.class)
public interface CommentMapper {

  CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

  @Mapping(target = "updatedDate", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  Comment fromNewComment(NewCommentDTO newComment, UUID postId);

  @Mapping(target = "id", expression = "java(CommentFactory.INSTANCE.createTransientId(comment))")
  @Mapping(target = "revisionCreatedBy", ignore = true)
  @Mapping(target = "revisionCreatedDate", ignore = true)
  CommentRevision fromComment(Comment comment, RevisionType revisionType);
}
