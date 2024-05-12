package com.vulinh.event;

import com.vulinh.constant.EndpointConstant;
import com.vulinh.constant.EndpointConstant.AuthEndpoint;
import com.vulinh.data.dto.event.UserRegistrationEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomEventListener {

  private static final String USER_REGISTRATION_LOG_MESSAGE =
      """
      Sending email to {}

      Registering link:

      {}{}?userId={}&code={}
      """;

  @EventListener
  public void listenUserRegistrationEvent(UserRegistrationEventDTO userRegistrationEventDTO) {
    var user = userRegistrationEventDTO.user();

    log.info(
        USER_REGISTRATION_LOG_MESSAGE,
        user.getEmail(),
        EndpointConstant.ENDPOINT_AUTH,
        AuthEndpoint.CONFIRM_USER,
        user.getId(),
        user.getUserRegistrationCode());
  }
}
