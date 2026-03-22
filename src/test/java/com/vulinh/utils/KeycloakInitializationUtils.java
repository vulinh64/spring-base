package com.vulinh.utils;

import module java.base;

import com.vulinh.Constants;
import com.vulinh.data.constant.UserRole;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StopWatch;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KeycloakInitializationUtils {

  static final String KC_ADM_SHELL = "/opt/keycloak/bin/kcadm.sh";

  static final Map<String, String> REPLACEMENT_MAP =
      Map.ofEntries(
          Map.entry("KC_ADMIN_USERNAME", Constants.KC_ADMIN_USERNAME),
          Map.entry("KC_ADMIN_PASSWORD", Constants.KC_ADMIN_PASSWORD),
          Map.entry("KEYCLOAK_REALM", Constants.KC_REALM),
          Map.entry("CLIENT_NAME", Constants.KC_CLIENT),
          Map.entry("ROLE_ADMIN", UserRole.ADMIN.name()),
          Map.entry("ROLE_POWER_USER", UserRole.POWER_USER.name()),
          Map.entry("ADMIN_USERNAME", Constants.TEST_ADMIN),
          Map.entry("POWER_USER_USERNAME", Constants.TEST_POWER_USER),
          Map.entry("COMMON_PASSWORD", Constants.COMMON_PASSWORD),
          Map.entry("KC_ADM_SHELL", KC_ADM_SHELL));

  @SneakyThrows
  public static void initializeKeycloak(KeycloakContainer keycloakContainer) {
    var clock = new StopWatch();

    clock.start();

    IO.println("Initializing Keycloak data, please wait for a while...");

    var clientAtomic = new AtomicReference<String>();

    var commands = KeycloakShellCommandUtils.readKeycloakExecCommands(REPLACEMENT_MAP);

    for (var command : commands) {
      var possibleUuid = clientAtomic.get();

      if (possibleUuid != null) {
        command =
            Arrays.stream(command)
                .map(s -> s.replace(wrapByBrackets("CLIENT_UUID"), possibleUuid))
                .toArray(String[]::new);
      }

      var result = keycloakContainer.execInContainer(command);

      var singleCommand = String.join(" ", command);

      var output =
          StringUtils.defaultIfBlank(
                  StringUtils.defaultIfBlank(result.getStdout(), result.getStderr()), singleCommand)
              .replace("\r", StringUtils.EMPTY)
              .replace("\n", StringUtils.EMPTY);

      IO.println(output);

      if (singleCommand.contains("create clients -r")) {
        clientAtomic.set(output);
      }
    }

    clock.stop();

    IO.println(
        "Finished Keycloak data initialization! Took %d ns (%f seconds)."
            .formatted(clock.getTotalTimeNanos(), clock.getTotalTimeSeconds()));
  }

  public static @NotNull String wrapByBrackets(String variable) {
    return "{{%s}}".formatted(variable);
  }
}
