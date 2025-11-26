package com.vulinh.controller.impl;

import module java.base;

import com.vulinh.controller.api.AuthAPI;
import com.vulinh.data.dto.carrier.TokenResponse;
import com.vulinh.data.dto.request.PasswordChangeRequest;
import com.vulinh.data.dto.request.RefreshTokenRequest;
import com.vulinh.data.dto.request.UserLoginRequest;
import com.vulinh.data.dto.request.UserRegistrationRequest;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.dto.response.GenericResponse.ResponseCreator;
import com.vulinh.data.dto.response.SingleUserResponse;
import com.vulinh.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthAPI {

  final AuthService authService;

  @Override
  public GenericResponse<TokenResponse> login(UserLoginRequest userLoginRequest) {
    return ResponseCreator.success(authService.login(userLoginRequest));
  }

  @Override
  public GenericResponse<SingleUserResponse> register(
      UserRegistrationRequest userRegistrationRequest) {
    throw deprecatedException("register");
  }

  @Override
  public ResponseEntity<GenericResponse<Object>> confirmUser(UUID userId, String code) {
    throw deprecatedException("confirmUser");
  }

  @Override
  public ResponseEntity<Object> changePassword(
      PasswordChangeRequest passwordChangeRequest, HttpServletRequest httpServletRequest) {
    throw deprecatedException("changePassword");
  }

  @Override
  public GenericResponse<TokenResponse> refreshToken(RefreshTokenRequest refreshTokenRequest) {
    throw deprecatedException("refreshToken");
  }

  @Override
  public ResponseEntity<Void> logout(String authorization) {
    throw deprecatedException("logout");
  }

  static UnsupportedOperationException deprecatedException(String what) {
    return new UnsupportedOperationException(what);
  }
}
