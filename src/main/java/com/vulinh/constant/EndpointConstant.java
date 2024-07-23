package com.vulinh.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EndpointConstant {

  public static final String ENDPOINT_AUTH = "/auth";
  public static final String ENDPOINT_USER = "/user";
  public static final String ENDPOINT_PASSWORD = "/password";
  public static final String ENDPOINT_POST = "/post";
  public static final String ENDPOINT_CATEGORY = "/category";
  public static final String ENDPOINT_FREE = "/free";

  public static final String COMMON_SEARCH_ENDPOINT = "/search";

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
    public static final String SEARCH = COMMON_SEARCH_ENDPOINT;
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

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class CategoryEndpoint {

    public static final String SEARCH = COMMON_SEARCH_ENDPOINT;
  }
}
