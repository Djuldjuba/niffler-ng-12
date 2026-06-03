package guru.qa.niffler.service.api;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.Assertions;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Optional;

public class SpendApiClient implements SpendClient {

  private static final Config CFG = Config.getInstance();

  private final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(CFG.spendUrl())
      .addConverterFactory(JacksonConverterFactory.create())
      .build();

  private final SpendApi spendApi = retrofit.create(SpendApi.class);

  @Override
  public SpendJson createSpending(SpendJson spending) {
    try {
      Response<SpendJson> response = spendApi.addSpend(spending)
          .execute();

      Assertions.assertEquals(201, response.code());
      return response.body();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public CategoryJson createCategory(CategoryJson category) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public Optional<CategoryJson> findByUsernameAndName(String username, String category) {
    throw new UnsupportedOperationException("Not implemented");
  }
}
