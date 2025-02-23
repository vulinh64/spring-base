package com.vulinh.controller.impl;

import com.vulinh.controller.api.AuthAPI;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.auth.PasswordChangeDTO;
import com.vulinh.data.dto.auth.RefreshTokenRequestDTO;
import com.vulinh.data.dto.auth.UserLoginDTO;
import com.vulinh.data.dto.auth.UserRegistrationDTO;
import com.vulinh.data.dto.security.TokenResponse;
import com.vulinh.data.dto.user.UserDTO;
import com.vulinh.factory.GenericResponseFactory;
import com.vulinh.locale.CommonMessage;
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

  private static final GenericResponseFactory RESPONSE_FACTORY = GenericResponseFactory.INSTANCE;

  private final AuthService authService;

  @Override
  public GenericResponse<TokenResponse> login(UserLoginDTO userLoginDTO) {
    return RESPONSE_FACTORY.success(authService.login(userLoginDTO));
  }

  @Override
  public GenericResponse<UserDTO> register(UserRegistrationDTO userRegistrationDTO) {
    return RESPONSE_FACTORY.success(authService.registerUser(userRegistrationDTO));
  }

  @Override
  public ResponseEntity<GenericResponse<Object>> confirmUser(UUID userId, String code) {
    boolean isUserConfirmed = authService.confirmUser(userId, code);

    return isUserConfirmed
        ? ResponseEntity.ok(GenericResponseFactory.INSTANCE.success())
        : ResponseEntity.status(CommonMessage.MESSAGE_INVALID_CONFIRMATION.getHttpStatusCode())
            .body(RESPONSE_FACTORY.toGenericResponse(CommonMessage.MESSAGE_INVALID_CONFIRMATION));
  }

  @Override
  public ResponseEntity<Object> changePassword(
      PasswordChangeDTO passwordChangeDTO, HttpServletRequest httpServletRequest) {
    authService.changePassword(passwordChangeDTO, httpServletRequest);

    return ResponseEntity.ok().build();
  }

  @Override
  public GenericResponse<TokenResponse> refreshToken(
      RefreshTokenRequestDTO refreshTokenRequestDTO) {
    return RESPONSE_FACTORY.success(authService.refreshToken(refreshTokenRequestDTO));
  }

  @Override
  public ResponseEntity<Void> logout(String authorization) {
    return ResponseEntity.status(
            authService.logout(authorization) ? HttpStatus.OK : HttpStatus.NO_CONTENT)
        .build();
  }
}
