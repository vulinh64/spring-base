package com.vulinh.data.mapper;

import com.vulinh.data.dto.comment.NewCommentReplyDTO;
import com.vulinh.data.entity.Comment;
import com.vulinh.data.entity.Reply;
import com.vulinh.data.entity.Users;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(builder = @Builder(disableBuilder = true))
public interface ReplyMapper {

  ReplyMapper INSTANCE = Mappers.getMapper(ReplyMapper.class);

  @Mapping(target = "updatedDate", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "content", source = "newCommentReplyDTO.content")
  @Mapping(target = "createdBy", source = "createdBy")
  Reply fromNewCommentReply(
      NewCommentReplyDTO newCommentReplyDTO, Comment comment, Users createdBy);
}
