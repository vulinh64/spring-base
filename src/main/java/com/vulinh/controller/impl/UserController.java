package com.vulinh.controller.impl;

import com.vulinh.controller.api.UserAPI;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.auth.UserRegistrationDTO;
import com.vulinh.data.dto.user.UserBasicDTO;
import com.vulinh.data.dto.user.UserDTO;
import com.vulinh.data.dto.user.UserSearchDTO;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.factory.GenericResponseFactory;
import com.vulinh.service.user.UserService;
import com.vulinh.utils.ResponseUtils;
import com.vulinh.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController implements UserAPI {

  private static final GenericResponseFactory RESPONSE_FACTORY = GenericResponseFactory.INSTANCE;

  private final UserService userService;

  @Override
  public GenericResponse<UserDTO> createUser(UserRegistrationDTO userRegistrationDTO) {
    return RESPONSE_FACTORY.success(userService.createUser(userRegistrationDTO));
  }

  @Override
  public ResponseEntity<Void> deleteUser(UUID id, HttpServletRequest httpServletRequest) {
    return ResponseUtils.returnOkOrNoContent(userService.delete(id, httpServletRequest));
  }

  @Override
  public ResponseEntity<GenericResponse<UserBasicDTO>> getSelfDetail(
      HttpServletRequest httpServletRequest) {
    return SecurityUtils.getUserDTO(httpServletRequest)
        .map(RESPONSE_FACTORY::success)
        .map(ResponseEntity::ok)
        .orElseThrow(ExceptionFactory.INSTANCE::invalidAuthorization);
  }

  @Override
  public GenericResponse<Page<UserDTO>> search(UserSearchDTO userSearchDTO, Pageable pageable) {
    return RESPONSE_FACTORY.success(userService.search(userSearchDTO, pageable));
  }
}
