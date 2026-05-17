package com.vulinh.service;

import module java.base;

import com.vulinh.service.event.EventService;
import com.vulinh.service.post.PostValidationService;
import com.vulinh.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSubscriptionService {

  final PostValidationService postValidationService;

  final EventService eventService;

  public boolean subscribeToUser(UUID subscribedUserId) {
    var actionUser = SecurityUtils.getUserDTOOrThrow();

    if (subscribedUserId.equals(actionUser.id())) {
      log.info("Cannot subscribe to oneself: {}", actionUser);

      return false;
    }

    eventService.sendSubscribeToUserEvent(actionUser, subscribedUserId);

    return true;
  }

  // There will be the feature to turn off notification in the future
  public boolean subscribeToPost(UUID postId) {
    var post = postValidationService.getPost(postId);

    var actionUser = SecurityUtils.getUserDTOOrThrow();

    if (PostValidationService.isOwner(actionUser, post)) {
      log.info(
          "Owner [{}] - [{}] already subscribed to their own post [{}} - [ {} ] by default",
          actionUser.id(),
          actionUser.username(),
          post.getId(),
          post.getTitle());

      return false;
    }

    eventService.sendNewPostFollowingEvent(post, actionUser);

    return true;
  }
}
