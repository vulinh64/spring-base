package com.vulinh.controller.impl;

import com.vulinh.controller.AuthAPI;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.auth.PasswordChangeDTO;
import com.vulinh.data.dto.auth.UserLoginDTO;
import com.vulinh.data.dto.auth.UserRegistrationDTO;
import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.security.AccessToken;
import com.vulinh.data.dto.user.UserDTO;
import com.vulinh.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthAPI {

  private final AuthService authService;

  @Override
  public GenericResponse<AccessToken> login(UserLoginDTO userLoginDTO) {
    return GenericResponse.success(authService.login(userLoginDTO));
  }

  @Override
  public GenericResponse<UserDTO> register(UserRegistrationDTO userRegistrationDTO) {
    return GenericResponse.success(authService.registerUser(userRegistrationDTO));
  }

  @Override
  public ResponseEntity<GenericResponse<Object>> confirmUser(String userId, String code) {
    boolean isUserConfirmed = authService.confirmUser(userId, code);

    return isUserConfirmed
        ? ResponseEntity.ok(GenericResponse.success())
        : ResponseEntity.status(CommonMessage.MESSAGE_INVALID_CONFIRMATION.getHttpStatusCode())
            .body(GenericResponse.toGenericResponse(CommonMessage.MESSAGE_INVALID_CONFIRMATION));
  }

  @Override
  public ResponseEntity<Object> changePassword(
          PasswordChangeDTO passwordChangeDTO, HttpServletRequest httpServletRequest) {
    authService.changePassword(passwordChangeDTO, httpServletRequest);

    return ResponseEntity.ok().build();
  }
}
