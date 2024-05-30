package com.vulinh.utils;

import com.vulinh.configuration.VerbUrl;
import com.vulinh.constant.EndpointConstant;
import com.vulinh.constant.EndpointConstant.UserEndpoint;
import com.vulinh.constant.UserRole;
import com.vulinh.factory.VerbUrlFactory;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUrlUtils {

  @NonNull
  public static String[] getUrlsWithPrivilege(UserRole userRole) {
    return HIGH_PRIVILEGES_URLS.getOrDefault(userRole, new String[] {});
  }

  @NonNull
  public static List<VerbUrl> getVerbUrlsWithPrivilege(UserRole userRole) {
    return HIGH_PRIVILEGES_VERB_URL.getOrDefault(userRole, Collections.emptyList());
  }

  private static final Map<UserRole, String[]> HIGH_PRIVILEGES_URLS =
      Map.ofEntries(
          Map.entry(
              UserRole.ADMIN,
              new String[] {
                combineDouble(EndpointConstant.ENDPOINT_USER, UserEndpoint.CREATE_USER),
                combineDouble(EndpointConstant.ENDPOINT_USER, UserEndpoint.DELETE_USER),
                combineDouble(EndpointConstant.ENDPOINT_USER, UserEndpoint.SEARCH)
              }));

  private static final Map<UserRole, List<VerbUrl>> HIGH_PRIVILEGES_VERB_URL =
      Map.ofEntries(
          Map.entry(
              UserRole.ADMIN,
              List.of(
                  VerbUrlFactory.INSTANCE.of(
                      HttpMethod.DELETE, "%s/**".formatted(EndpointConstant.ENDPOINT_CATEGORY)))));

  private static String combineDouble(Object... path) {
    return "%s%s/**".formatted(path);
  }
}
