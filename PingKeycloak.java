public class PingKeycloak {

  public static void main(String[] args) {
    try {
      java.net.URL url = new java.net.URI(
          "http://localhost:8080/realms/spring-base/.well-known/openid-configuration")
          .toURL();
      java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
      conn.setConnectTimeout(5000);
      conn.setReadTimeout(5000);
      System.exit(conn.getResponseCode() == 200 ? 0 : 1);
    } catch (Exception e) {
      System.exit(1);
    }
  }
}
