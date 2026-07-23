package guru.qa.niffler.service.api;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.Assertions;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendApiClient implements SpendClient {

  private static final Config CFG = Config.getInstance();

  private final Retrofit retrofit = new Retrofit.Builder()
          .baseUrl(CFG.spendUrl())
          .addConverterFactory(JacksonConverterFactory.create())
          .build();

  private final SpendApi spendApi = retrofit.create(SpendApi.class);

  @Override
  public SpendJson createSpend(SpendJson spending) {
    try {
      Response<SpendJson> response = spendApi.addSpend(spending).execute();
      Assertions.assertEquals(201, response.code());
      return response.body();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public SpendJson updateSpend(SpendJson spend) {
    return null;
  }

  @Override
  public CategoryJson createCategory(CategoryJson category) {
    return null;
  }

  @Override
  public Optional<CategoryJson> findCategoryById(UUID id) {
    return Optional.empty();
  }

  @Override
  public Optional<CategoryJson> findCategoryByUsernameAndName(String username, String name) {
    return Optional.empty();
  }

  @Override
  public Optional<SpendJson> findSpendById(UUID id) {
    return Optional.empty();
  }

  @Override
  public Optional<SpendJson> findSpendByUsernameAndDescription(String username, String description) {
    return Optional.empty();
  }

  @Override
  public void deleteSpend(SpendJson spend) {

  }

  @Override
  public void deleteCategory(CategoryJson category) {

  }

  public List<CategoryJson> getCategories(String username, boolean excludeArchived) {
    try {
      Response<List<CategoryJson>> response = spendApi.getCategories(username, excludeArchived).execute();
      if (response.isSuccessful() && response.body() != null) {
        return response.body();
      }
      return List.of();
    } catch (IOException e) {
      throw new RuntimeException("Failed to get categories for user: " + username, e);
    }
  }

  public List<SpendJson> getSpends(String username, CurrencyValues filterCurrency, Date from, Date to) {
    try {
      Response<List<SpendJson>> response = spendApi.getSpends(username, filterCurrency, from, to).execute();
      if (response.isSuccessful() && response.body() != null) {
        return response.body();
      }
      return List.of();
    } catch (IOException e) {
      throw new RuntimeException("Failed to get spends for user: " + username, e);
    }
  }
}