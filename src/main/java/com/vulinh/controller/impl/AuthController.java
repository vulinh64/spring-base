package com.vulinh.controller.impl;

import com.vulinh.controller.api.AuthAPI;
import com.vulinh.data.dto.carrier.TokenResponse;
import com.vulinh.data.dto.request.PasswordChangeRequest;
import com.vulinh.data.dto.request.RefreshTokenRequest;
import com.vulinh.data.dto.request.UserLoginRequest;
import com.vulinh.data.dto.request.UserRegistrationRequest;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.dto.response.GenericResponse.ResponseCreator;
import com.vulinh.data.dto.response.SingleUserResponse;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthAPI {

  private final AuthService authService;

  @Override
  public GenericResponse<TokenResponse> login(UserLoginRequest userLoginRequest) {
    return ResponseCreator.success(authService.login(userLoginRequest));
  }

  @Override
  public GenericResponse<SingleUserResponse> register(
      UserRegistrationRequest userRegistrationRequest) {
    return ResponseCreator.success(authService.registerUser(userRegistrationRequest));
  }

  @Override
  public ResponseEntity<GenericResponse<Object>> confirmUser(UUID userId, String code) {
    boolean isUserConfirmed = authService.confirmUser(userId, code);

    return isUserConfirmed
        ? ResponseEntity.ok(ResponseCreator.success())
        : ResponseEntity.badRequest()
            .body(ResponseCreator.toError(ServiceErrorCode.MESSAGE_INVALID_CONFIRMATION));
  }

  @Override
  public ResponseEntity<Object> changePassword(
      PasswordChangeRequest passwordChangeRequest, HttpServletRequest httpServletRequest) {
    authService.changePassword(passwordChangeRequest);

    return ResponseEntity.ok().build();
  }

  @Override
  public GenericResponse<TokenResponse> refreshToken(RefreshTokenRequest refreshTokenRequest) {
    return ResponseCreator.success(authService.refreshToken(refreshTokenRequest));
  }

  @Override
  public ResponseEntity<Void> logout(String authorization) {
    return ResponseEntity.status(
            authService.logout(authorization) ? HttpStatus.OK : HttpStatus.NO_CONTENT)
        .build();
  }
}
