package com.vulinh.controller.impl;

import com.vulinh.controller.api.UserAPI;
import com.vulinh.data.dto.request.UserRegistrationRequest;
import com.vulinh.data.dto.request.UserSearchRequest;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.dto.response.GenericResponse.ResponseCreator;
import com.vulinh.data.dto.response.SingleUserResponse;
import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.exception.AuthorizationException;
import com.vulinh.service.user.UserService;
import com.vulinh.utils.ResponseUtils;
import com.vulinh.utils.SecurityUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController implements UserAPI {

  private final UserService userService;

  @Override
  public GenericResponse<SingleUserResponse> createUser(
      UserRegistrationRequest userRegistrationRequest) {
    return ResponseCreator.success(userService.createUser(userRegistrationRequest));
  }

  @Override
  public ResponseEntity<Void> deleteUser(UUID id) {
    return ResponseUtils.returnOkOrNoContent(userService.delete(id));
  }

  @Override
  public ResponseEntity<GenericResponse<UserBasicResponse>> getSelfDetail() {
    return SecurityUtils.getUserDTO()
        .map(ResponseCreator::success)
        .map(ResponseEntity::ok)
        .orElseThrow(AuthorizationException::invalidAuthorization);
  }

  @Override
  public GenericResponse<Page<SingleUserResponse>> search(
      UserSearchRequest userSearchRequest, Pageable pageable) {
    return ResponseCreator.success(userService.search(userSearchRequest, pageable));
  }
}
