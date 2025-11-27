package com.vulinh.data.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EndpointConstant {

  public static final String ENDPOINT_POST = "/post";
  public static final String ENDPOINT_CATEGORY = "/category";
  public static final String ENDPOINT_FREE = "/free";

  public static final String COMMON_SEARCH_ENDPOINT = "/search";
  public static final String ENDPOINT_COMMENT = "/comment";

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class PostEndpoint {

    public static final String POST_ID_VARIABLE = "postId";
    public static final String IDENTITY_VARIABLE = "identity";

    public static final String POST_ID_VARIABLE_FORMAT = "/{postId}";
    public static final String IDENTITY_VARIABLE_FORMAT = "/{identity}";

    public static final String REVISION_ENDPOINT = "/revisions";
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class CategoryEndpoint {

    public static final String SEARCH = COMMON_SEARCH_ENDPOINT;
  }
}
