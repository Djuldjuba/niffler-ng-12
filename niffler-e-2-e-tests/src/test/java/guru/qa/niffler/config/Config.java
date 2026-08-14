package guru.qa.niffler.config;

public interface Config {

  static Config getInstance() {
    return LocalConfig.INSTANCE;
  }

  String frontUrl();
  String authUrl();
  String authJdbcUrl();
  String gatewayUrl();
  String userdataUrl();
  String userdataJdbcUrl();
  String spendUrl();
  String spendJdbcUrl();
  String githubUrl();
  String currencyJdbcUrl();
  String dbUsername();
  String dbPassword();
  String webClientId();
  String currencyGrpcAddress();
  default int currencyGrpcPort() {
    return 8092;
  }
  default String userdataGrpcAddress() {
    return "localhost";
  }
  default int userdataGrpcPort() {
    return 8095;
  }
}