package com.vulinh.data.mapper;

import com.vulinh.data.dto.response.KeycloakUserResponse;
import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.data.entity.Comment;
import com.vulinh.data.entity.Post;
import com.vulinh.data.event.NewCommentEvent;
import com.vulinh.data.event.NewPostEvent;
import com.vulinh.data.event.NewSubscriptionEvent;
import org.apache.commons.lang3.BooleanUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(imports = BooleanUtils.class)
public interface EventMapper {

  EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

  @Mapping(target = "actionUserId", source = "actionUser.id")
  @Mapping(target = "postId", source = "post.id")
  @Mapping(target = "username", source = "actionUser.username")
  NewPostEvent toNewPostEvent(Post post, UserBasicResponse actionUser);

  @Mapping(target = "actionUserId", source = "actionUser.id")
  @Mapping(target = "subscribedUserId", source = "subscribedUser.id")
  @Mapping(target = "actionUsername", source = "actionUser.username")
  @Mapping(target = "subscribedUsername", source = "subscribedUser.username")
  NewSubscriptionEvent toNewSubscriptionEvent(
      UserBasicResponse actionUser, KeycloakUserResponse subscribedUser);

  @Mapping(target = "isEnabled", expression = "java(BooleanUtils.isTrue(user.isEnabled()))")
  KeycloakUserResponse toKeycloakUserResponse(UserRepresentation user);

  @Mapping(target = "actionUserId", source = "actionUser.id")
  @Mapping(target = "comment", source = "comment.content")
  @Mapping(target = "postId", source = "post.id")
  NewCommentEvent toNewCommentEvent(Comment comment, Post post, UserBasicResponse actionUser);
}
