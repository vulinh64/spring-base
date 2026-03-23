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

  private final ApplicationProperties applicationProperties;

  private final KeycloakAuthExchange keycloakAuthExchange;

  private final AuthorService authorService;

  @Override
  public ResponseEntity<Void> login(LoginRequest loginRequest, HttpServletResponse response) {
    try {
      var tokenResponse =
          keycloakAuthExchange.getToken(
              applicationProperties.security(), loginRequest.username(), loginRequest.password());

      addCookie(response, "access_token", tokenResponse.accessToken(), 300);
      addCookie(response, "refresh_token", tokenResponse.refreshToken(), 1800);

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
  public ResponseEntity<Void> refresh(String refreshToken, HttpServletResponse response) {
    try {
      var tokenResponse =
          keycloakAuthExchange.refreshToken(applicationProperties.security(), refreshToken);

      addCookie(response, "access_token", tokenResponse.accessToken(), tokenResponse.expiresIn());
      addCookie(response, "refresh_token", tokenResponse.refreshToken(), tokenResponse.expiresIn());

      return ResponseEntity.ok().build();
    } catch (HttpClientErrorException.Unauthorized e) {
      throw new KeycloakAuthenticationException("refresh", e);
    }
  }

  @Override
  public ResponseEntity<Void> logout(HttpServletResponse response) {
    addCookie(response, "access_token", "", 0);
    addCookie(response, "refresh_token", "", 0);

    return ResponseEntity.ok().build();
  }

  private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
    var cookie = new Cookie(name, value);

    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge(maxAge);
    cookie.setAttribute("SameSite", "Strict");

    response.addCookie(cookie);
  }
}
