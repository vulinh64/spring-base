package com.vulinh.it;

import module java.base;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class KeycloakShellCommandUtils {

  @SneakyThrows
  public static List<String[]> readKeycloakExecCommands(
      Map<String, String> replacementMap) {
    var resource = new ClassPathResource("keycloak-exec.txt");

    try (var lines = Files.lines(resource.getFile().toPath())) {
      return lines
          .map(String::trim)
          .filter(Predicate.not(String::isEmpty))
          .map(
              cmd -> {
                for (var entry : replacementMap.entrySet()) {
                  cmd = cmd.replace(entry.getKey(), entry.getValue());
                }

                return cmd;
              })
          .map(line -> line.split("\\s+"))
          .toList();
    }
  }
}
