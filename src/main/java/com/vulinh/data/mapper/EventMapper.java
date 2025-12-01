package com.vulinh.data.mapper;

import com.vulinh.data.entity.Post;
import com.vulinh.data.event.NewPostEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EventMapper {

  EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

  @Mapping(target = "postId", source = "id")
  NewPostEvent toNewPostEvent(Post post);
}
