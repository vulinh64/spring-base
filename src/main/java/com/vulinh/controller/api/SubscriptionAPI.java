package com.vulinh.controller.api;

import module java.base;

import com.vulinh.data.constant.CommonConstant;
import com.vulinh.data.constant.EndpointConstant;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(EndpointConstant.ENDPOINT_SUBSCRIPTION)
@Tag(name = "Subscription controller", description = "Manage subscriptions")
public interface SubscriptionAPI {

  @PostMapping("/user/{userId}")
  @SecurityRequirement(name = CommonConstant.SECURITY_SCHEME_NAME)
  ResponseEntity<Void> subscribeToUser(@PathVariable UUID userId);
}
