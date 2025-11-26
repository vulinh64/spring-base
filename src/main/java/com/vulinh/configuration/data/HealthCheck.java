package com.vulinh.configuration.data;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

// This should be much much simpler with JDK 25
public class HealthCheck {

  static final int RETURN_OK = 0;
  static final int RETURN_ERROR = -1;

  public static void main(String[] args) {
    try {
      System.exit(doHealthCheck(URI.create(parseKeycloakHealthCheckUrl())));
    } catch (Exception ignored) {
      // Shut up and give me candy
      System.exit(RETURN_ERROR);
    }
  }

  private static String parseKeycloakHealthCheckUrl() {
    var fromEnv = System.getenv("HEALTHCHECK_URI");

    return fromEnv == null || fromEnv.isBlank() ? "http://localhost:9000/health/live" : fromEnv;
  }

  private static int doHealthCheck(URI uri) throws IOException {
    if (!(uri.toURL().openConnection() instanceof HttpURLConnection httpConnection)) {
      return RETURN_ERROR;
    }

    return HttpURLConnection.HTTP_OK == httpConnection.getResponseCode() ? RETURN_OK : RETURN_ERROR;
  }
}
