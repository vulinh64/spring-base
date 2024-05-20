package com.vulinh.constant;

import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EndpointConstant {

  public static final String ENDPOINT_AUTH = "/auth";
  public static final String ENDPOINT_USER = "/user";
  public static final String ENDPOINT_PASSWORD = "/password";
  public static final String ENDPOINT_POST = "/post";

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class AuthEndpoint {

    public static final String LOGIN = "/login";
    public static final String REGISTER = "/register";
    public static final String CONFIRM_USER = "/confirm-user";
    public static final String CHANGE_PASSWORD = "/change-password";
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class UserEndpoint {

    public static final String CREATE_USER = "/create-user";
    public static final String DELETE_USER = "/delete-user";
    public static final String DETAILS = "/details";
    public static final String SEARCH = "/search";
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class PostEndpoint {

    public static final String POST_ID_VARIABLE = "postId";
    public static final String IDENTITY_VARIABLE = "identity";

    public static final String POST_ID_VARIABLE_FORMAT = "/{postId}";
    public static final String IDENTITY_VARIABLE_FORMAT = "/{identity}";

    public static final String REVISION_ENDPOINT = "/revisions";
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class BcryptEndpoint {

    public static final String GENERATE = "/generate";
  }

  @NonNull
  public static String[] getUrlsWithPrivilege(@NonNull UserRole userRole) {
    return HIGH_PRIVILEGES_URLS.getOrDefault(userRole, new String[] {});
  }

  private static final Map<UserRole, String[]> HIGH_PRIVILEGES_URLS =
      Map.ofEntries(
          Map.entry(
              UserRole.ADMIN,
              new String[] {
                combine(ENDPOINT_USER, UserEndpoint.CREATE_USER),
                combine(ENDPOINT_USER, UserEndpoint.DELETE_USER),
                combine(ENDPOINT_USER, UserEndpoint.SEARCH)
              }));

  private static String combine(Object... path) {
    return "%s%s/**".formatted(path);
  }
}
