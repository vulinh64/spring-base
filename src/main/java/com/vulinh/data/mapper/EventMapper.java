package com.vulinh.data.mapper;

import com.vulinh.data.dto.response.KeycloakUserResponse;
import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.data.entity.Comment;
import com.vulinh.data.entity.Post;
import com.vulinh.data.event.*;
import com.vulinh.data.event.payload.NewCommentEvent;
import com.vulinh.data.event.payload.NewPostEvent;
import com.vulinh.data.event.payload.NewPostFollowingEvent;
import com.vulinh.data.event.payload.NewSubscriberEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EventMapper {

  EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

  @Mapping(target = "postId", source = "post.id")
  NewPostEvent toNewPostEvent(Post post);

  @Mapping(target = "postId", source = "post.id")
  NewPostFollowingEvent toNewPostFollowingEvent(Post post);

  @Mapping(target = "subscribedUsername", source = "subscribedUser.username")
  @Mapping(target = "subscribedUserId", source = "subscribedUser.id")
  NewSubscriberEvent toNewSubscriptionEvent(KeycloakUserResponse subscribedUser);

  @Mapping(target = "commentId", source = "comment.id")
  @Mapping(target = "postId", source = "post.id")
  NewCommentEvent toNewCommentEvent(Comment comment, Post post);

  ActionUser toActionUser(UserBasicResponse basicActionUser);
}
