package com.vulinh.controller.impl;

import com.vulinh.controller.api.AuthAPI;
import com.vulinh.data.dto.carrier.TokenResponse;
import com.vulinh.data.dto.request.UserLoginRequest;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.dto.response.GenericResponse.ResponseCreator;
import com.vulinh.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthAPI {

  final AuthService authService;

  @Override
  public GenericResponse<TokenResponse> login(UserLoginRequest userLoginRequest) {
    return ResponseCreator.success(authService.login(userLoginRequest));
  }
}
