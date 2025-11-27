package com.vulinh.it;

import module java.base;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class KeycloakShellCommandUtils {

  @SneakyThrows
  public static List<String[]> readKeycloakExecCommands(Map<String, String> replacementMap) {
    try (var lines = Files.lines(new ClassPathResource("keycloak-exec.txt").getFile().toPath())) {
      return lines
          .filter(Predicate.not(StringUtils::isBlank))
          .map(StringUtils::normalizeSpace)
          .map(
              command -> {
                for (var entry : replacementMap.entrySet()) {
                  command = command.replace(wrapByBrackets(entry.getKey()), entry.getValue());
                }

                return command.split("\\s+");
              })
          .toList();
    }
  }

  public static @NotNull String wrapByBrackets(String variable) {
    return "{{%s}}".formatted(variable);
  }
}
