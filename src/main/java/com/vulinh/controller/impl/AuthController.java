package com.vulinh.controller.impl;

import com.vulinh.configuration.data.ApplicationProperties;
import com.vulinh.controller.api.AuthAPI;
import com.vulinh.data.dto.request.LoginRequest;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.dto.response.GenericResponse.ResponseCreator;
import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.exception.KeycloakAuthenticationException;
import com.vulinh.service.author.AuthorService;
import com.vulinh.service.keycloak.KeycloakAuthExchange;
import com.vulinh.utils.SecurityUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthAPI {

  private final KeycloakAuthExchange keycloakAuthExchange;
  private final ApplicationProperties applicationProperties;
  private final AuthorService authorService;

  @Override
  public ResponseEntity<Void> login(LoginRequest loginRequest, HttpServletResponse response) {
    try {
      var tokenResponse =
          keycloakAuthExchange.getToken(
              applicationProperties.security(), loginRequest.username(), loginRequest.password());

      addCookie(response, tokenResponse.accessToken(), 300);

      authorService.populateAuthorAsync(tokenResponse.accessToken());

      return ResponseEntity.ok().build();
    } catch (HttpClientErrorException.Unauthorized e) {
      throw new KeycloakAuthenticationException(loginRequest.username(), e);
    }
  }

  @Override
  public GenericResponse<UserBasicResponse> me() {
    return ResponseCreator.success(SecurityUtils.getUserDTOOrThrow());
  }

  @Override
  public ResponseEntity<Void> logout(HttpServletResponse response) {
    addCookie(response, "", 0);

    return ResponseEntity.ok().build();
  }

  private void addCookie(HttpServletResponse response, String value, int maxAge) {
    var cookie = new Cookie("access_token", value);

    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge(maxAge);
    cookie.setAttribute("SameSite", "Strict");

    response.addCookie(cookie);
  }
}
