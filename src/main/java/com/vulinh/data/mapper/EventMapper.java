package com.vulinh.data.mapper;

import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.data.entity.Post;
import com.vulinh.data.event.NewPostEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EventMapper {

  EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

  @Mapping(target = "timestamp", ignore = true)
  @Mapping(target = "eventId", ignore = true)
  @Mapping(target = "postId", source = "post.id")
  @Mapping(target = "username", source = "userBasicResponse.username")
  NewPostEvent toNewPostEvent(Post post, UserBasicResponse userBasicResponse);
}
