package com.vulinh.service.eventlistener;

import com.vulinh.data.constant.EndpointConstant;
import com.vulinh.data.constant.EndpointConstant.AuthEndpoint;
import com.vulinh.data.dto.event.UserRegistrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomEventListener {

  @EventListener
  public void listenUserRegistrationEvent(UserRegistrationEvent userRegistrationEvent) {
    var user = userRegistrationEvent.user();

    log.debug(
        """
        {}

        Sending email to {}

        Registering link: {}{}?userId={}&code={}
        """,
        Thread.currentThread(),
        user.getEmail(),
        EndpointConstant.ENDPOINT_AUTH,
        AuthEndpoint.CONFIRM_USER,
        user.getId(),
        user.getUserRegistrationCode());
  }
}
