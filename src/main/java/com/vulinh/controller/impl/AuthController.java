package com.vulinh.controller.impl;

import com.vulinh.controller.api.AuthAPI;
import com.vulinh.data.dto.request.LoginRequest;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.service.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthAPI {

  private final AuthService authService;

  @Override
  public void login(LoginRequest loginRequest, HttpServletResponse response) {
    authService.login(loginRequest, response);
  }

  @Override
  public GenericResponse<UserBasicResponse> me() {
    return authService.me();
  }

  @Override
  public void refresh(String refreshToken, HttpServletResponse response) {
    authService.refresh(refreshToken, response);
  }

  @Override
  public void logout(HttpServletResponse response) {
    authService.logout(response);
  }
}
