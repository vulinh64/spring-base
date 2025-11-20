package com.vulinh.controller.impl;

import module java.base;

import com.vulinh.controller.api.UserAPI;
import com.vulinh.data.dto.request.UserRegistrationRequest;
import com.vulinh.data.dto.request.UserSearchRequest;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.dto.response.GenericResponse.ResponseCreator;
import com.vulinh.data.dto.response.SingleUserResponse;
import com.vulinh.service.user.UserService;
import com.vulinh.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController implements UserAPI {

  final UserService userService;

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
  public GenericResponse<Page<SingleUserResponse>> search(
      UserSearchRequest userSearchRequest, Pageable pageable) {
    return ResponseCreator.success(userService.search(userSearchRequest, pageable));
  }
}
