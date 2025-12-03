package com.vulinh.controller.impl;

import module java.base;

import com.vulinh.controller.api.SubscriptionAPI;
import com.vulinh.service.event.UserSubscriptionService;
import com.vulinh.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SubscriptionController implements SubscriptionAPI {

  final UserSubscriptionService userSubscriptionService;

  @Override
  public ResponseEntity<Void> subscribeToUser(UUID userId) {
    return ResponseUtils.returnOkOrNoContent(userSubscriptionService.subscribeToUser(userId));
  }
}
