package com.vulinh.controller.api;

import com.vulinh.data.constant.CommonConstant;
import com.vulinh.data.constant.EndpointConstant;
import com.vulinh.data.dto.request.LoginRequest;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.dto.response.UserBasicResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@RequestMapping(EndpointConstant.ENDPOINT_AUTH)
@Tag(name = "Auth controller", description = "Authentication via Keycloak token exchange")
public interface AuthAPI {

  @PostMapping("/login")
  void login(@RequestBody LoginRequest loginRequest, HttpServletResponse response);

  @PostMapping("/logout")
  void logout(HttpServletResponse response);

  @PostMapping("/refresh")
  void refresh(@CookieValue("refresh_token") String refreshToken, HttpServletResponse response);

  @GetMapping("/me")
  @SecurityRequirement(name = CommonConstant.SECURITY_SCHEME_NAME)
  GenericResponse<UserBasicResponse> me();
}
