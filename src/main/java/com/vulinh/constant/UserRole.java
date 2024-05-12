package com.vulinh.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.stream.Collectors;

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

  public static final Collection<String> DEFAULT_ROLE = Set.of(UserRole.USER.name());

  public static String toHierarchyPhrase() {
    return Arrays.stream(values())
        .sorted(
            Comparator.comparing(
                UserRole::superiority, Comparator.nullsLast(Comparator.reverseOrder())))
        .map(UserRole::name)
        .collect(Collectors.joining(" > "));
  }
}
