package com.vulinh.constant;

import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum UserRole {
  ADMIN(Integer.MAX_VALUE),
  POWER_USER(Integer.MAX_VALUE - 1),
  USER(0),
  INVALID(Integer.MIN_VALUE);

  // The higher the value, the more "superior" a role is
  private final int superiority;

  private static final Set<String> ROLE_LITERAL_SET =
      Arrays.stream(values()).map(UserRole::name).collect(Collectors.toSet());

  public static Collection<UserRole> fromRawRole(Collection<String> rawRoles) {
    return rawRoles.stream()
        .filter(UserRole::isValidRole)
        .map(String::toUpperCase)
        .map(UserRole::valueOf)
        .collect(Collectors.toSet());
  }

  public static boolean isValidRole(String role) {
    return ROLE_LITERAL_SET.stream().anyMatch(userRole -> userRole.equalsIgnoreCase(role));
  }

  public static String toHierarchyPhrase() {
    return Arrays.stream(values())
        .sorted(
            Comparator.comparing(
                UserRole::superiority, Comparator.nullsLast(Comparator.reverseOrder())))
        .map(UserRole::name)
        .collect(Collectors.joining(" > "));
  }
}
