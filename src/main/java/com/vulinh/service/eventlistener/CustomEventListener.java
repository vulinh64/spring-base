package com.vulinh.service.eventlistener;

import com.vulinh.constant.EndpointConstant;
import com.vulinh.constant.EndpointConstant.AuthEndpoint;
import com.vulinh.data.dto.event.UserRegistrationEventDTO;
import com.vulinh.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomEventListener {

  private final CategoryService categoryService;

  @EventListener
  public void listenUserRegistrationEvent(UserRegistrationEventDTO userRegistrationEventDTO) {
    var user = userRegistrationEventDTO.user();

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

  @EventListener
  public void applicationStartedEventListener(ContextRefreshedEvent contextRefreshedEvent) {
    categoryService.initializeFirstCategory();
  }
}
