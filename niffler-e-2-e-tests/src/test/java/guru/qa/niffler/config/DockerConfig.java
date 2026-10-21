package guru.qa.niffler.config;

enum DockerConfig implements Config {
  INSTANCE;

  @Override
  public String frontUrl() {
    return "http://frontend.niffler.dc/";
  }

  @Override
  public String authUrl() {
    return "http://auth.niffler.dc:9000/";
  }

  @Override
  public String authJdbcUrl() {
    return "jdbc:postgresql://niffler-all-db:5432/niffler-auth";
  }

  @Override
  public String gatewayUrl() {
    return "http://gateway.niffler.dc:8090/";
  }

  @Override
  public String userdataUrl() {
    return "http://userdata.niffler.dc:8089/";
  }

  @Override
  public String userdataJdbcUrl() {
    return "jdbc:postgresql://niffler-all-db:5432/niffler-userdata";
  }

  @Override
  public String spendUrl() {
    return "http://spend.niffler.dc:8093";
  }

  @Override
  public String spendJdbcUrl() {
    return "jdbc:postgresql://niffler-all-db:5432/niffler-spend";
  }

  @Override
  public String currencyJdbcUrl() {
    return "jdbc:postgresql://niffler-all-db:5432/niffler-currency";
  }

  @Override
  public String dbUsername() {
    return "postgres";
  }

  @Override
  public String dbPassword() {
    return "postgres";
  }

  @Override
  public String webClientId() {
    return "client";
  }

  @Override
  public String currencyGrpcAddress() {
    return "currency.niffler.dc";
  }

  @Override
  public String userdataGrpcAddress() {
    return "userdata.niffler.dc";
  }
}