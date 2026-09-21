package guru.qa.niffler.api;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.Date;
import java.util.List;

public interface SpendApi {

  @POST("/internal/spends/add")
  Call<SpendJson> addSpend(@Body SpendJson spendJson);

  @GET("/internal/categories/all")
  Call<List<CategoryJson>> getCategories(
          @Query("username") String username,
          @Query("excludeArchived") boolean excludeArchived
  );

  @GET("/internal/spends/all")
  Call<List<SpendJson>> getSpends(
          @Query("username") String username,
          @Query("filterCurrency") CurrencyValues filterCurrency,
          @Query("from") Date from,
          @Query("to") Date to
  );
}