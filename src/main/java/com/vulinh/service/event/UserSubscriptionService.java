package com.vulinh.service.event;

import module java.base;

import com.vulinh.exception.KeycloakUserDisabledException;
import com.vulinh.service.keycloak.KeycloakAdminClientService;
import com.vulinh.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSubscriptionService {

  final EventService eventService;

  final KeycloakAdminClientService keycloakAdminClientService;

  public boolean subscribeToUser(UUID subscribedUserId) {
    var actionUser = SecurityUtils.getUserDTOOrThrow();

    if (subscribedUserId.equals(actionUser.id())) {
      log.info("Cannot subscribe to oneself: {}", actionUser);

      return false;
    }

    var subscribedUser = keycloakAdminClientService.getKeycloakUser(subscribedUserId);

    if (BooleanUtils.isFalse(subscribedUser.isEnabled())) {
      throw KeycloakUserDisabledException.userDisabledException(subscribedUserId);
    }

    eventService.sendSubscribeToUserEvent(actionUser, subscribedUser);

    return true;
  }
}
