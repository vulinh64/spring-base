package com.vulinh.controller.api;

import com.vulinh.data.constant.CommonConstant;
import com.vulinh.data.constant.EndpointConstant;
import com.vulinh.data.dto.request.LoginRequest;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.dto.response.UserBasicResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(EndpointConstant.ENDPOINT_AUTH)
@Tag(name = "Auth controller", description = "Authentication via Keycloak token exchange")
public interface AuthAPI {

  @PostMapping("/login")
  ResponseEntity<Void> login(
      @RequestBody LoginRequest loginRequest, HttpServletResponse response);

  @PostMapping("/logout")
  ResponseEntity<Void> logout(HttpServletResponse response);

  @GetMapping("/me")
  @SecurityRequirement(name = CommonConstant.SECURITY_SCHEME_NAME)
  GenericResponse<UserBasicResponse> me();
}
