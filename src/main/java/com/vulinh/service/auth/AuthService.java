package com.vulinh.service.auth;

import com.vulinh.configuration.data.ApplicationProperties;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
public class AuthService {

  static final String ACCESS_TOKEN_COOKIE = "access_token";
  static final String REFRESH_TOKEN_COOKIE = "refresh_token";

  final ApplicationProperties applicationProperties;

  final AuthorService authorService;

  final KeycloakAuthExchange keycloakAuthExchange;

  public void login(LoginRequest loginRequest, HttpServletResponse response) {
    try {
      var tokenResponse =
          keycloakAuthExchange.getToken(
              applicationProperties.security(), loginRequest.username(), loginRequest.password());

      response.addCookie(
          addCookie(ACCESS_TOKEN_COOKIE, tokenResponse.accessToken(), tokenResponse.expiresIn()));
      response.addCookie(
          addCookie(
              REFRESH_TOKEN_COOKIE,
              tokenResponse.refreshToken(),
              tokenResponse.refreshExpiresIn()));

      authorService.populateAuthorAsync(tokenResponse.accessToken());
    } catch (HttpClientErrorException.Unauthorized e) {
      throw new KeycloakAuthenticationException(loginRequest.username(), e);
    }
  }

  public GenericResponse<UserBasicResponse> me() {
    return ResponseCreator.success(SecurityUtils.getUserDTOOrThrow());
  }

  public void refresh(String refreshToken, HttpServletResponse response) {
    try {
      var tokenResponse =
          keycloakAuthExchange.refreshToken(applicationProperties.security(), refreshToken);

      response.addCookie(
          addCookie(ACCESS_TOKEN_COOKIE, tokenResponse.accessToken(), tokenResponse.expiresIn()));
      response.addCookie(
          addCookie(
              REFRESH_TOKEN_COOKIE,
              tokenResponse.refreshToken(),
              tokenResponse.refreshExpiresIn()));
    } catch (HttpClientErrorException.Unauthorized e) {
      clearCookies(response);
      throw new KeycloakAuthenticationException("refresh", e);
    }
  }

  public void logout(HttpServletResponse response) {
    clearCookies(response);
  }

  private static void clearCookies(HttpServletResponse response) {
    response.addCookie(addCookie(ACCESS_TOKEN_COOKIE, StringUtils.EMPTY, 0));
    response.addCookie(addCookie(REFRESH_TOKEN_COOKIE, StringUtils.EMPTY, 0));
  }

  private static Cookie addCookie(String name, String value, int maxAge) {
    var cookie = new Cookie(name, value);

    cookie.setMaxAge(maxAge);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setSecure(true);
    cookie.setAttribute("SameSite", "Strict");

    return cookie;
  }
}
