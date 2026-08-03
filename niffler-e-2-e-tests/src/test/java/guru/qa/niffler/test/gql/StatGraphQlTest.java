package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.niffler.StatQuery;
import guru.qa.niffler.jupiter.annotation.ApiLoginRest;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.type.CurrencyValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StatGraphQlTest extends BaseGraphQlTest {

    @Test
    @ApiLoginRest(username = "duck", password = "12345")
    void shouldGetStatInRub(@Token String bearerToken) {
        ApolloCall<StatQuery.Data> call = apolloClient.query(
                StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(CurrencyValues.RUB)
                        .filterPeriod(null)
                        .build()
        ).addHttpHeader("authorization", bearerToken);

        ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(call).blockingGet();
        StatQuery.Data data = response.dataOrThrow();

        Assertions.assertEquals(2793054.8, data.stat.total);
    }

    @Test
    @ApiLoginRest(username = "duck", password = "12345")
    void shouldGetStatInUsd(@Token String bearerToken) {
        ApolloCall<StatQuery.Data> call = apolloClient.query(
                StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(CurrencyValues.USD)
                        .filterPeriod(null)
                        .build()
        ).addHttpHeader("authorization", bearerToken);

        ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(call).blockingGet();
        StatQuery.Data data = response.dataOrThrow();

        Assertions.assertEquals(41895.82, data.stat.total);
    }

    @Test
    @ApiLoginRest(username = "duck", password = "12345")
    void shouldIncludeArchivedCategoriesInStat(@Token String bearerToken) {
        ApolloCall<StatQuery.Data> call = apolloClient.query(
                StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(CurrencyValues.RUB)
                        .filterPeriod(null)
                        .build()
        ).addHttpHeader("authorization", bearerToken);

        ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(call).blockingGet();
        StatQuery.Data data = response.dataOrThrow();

        List<String> categoryNames = data.stat.statByCategories.stream()
                .map(stat -> stat.categoryName)
                .collect(Collectors.toList());

        Assertions.assertTrue(
                categoryNames.contains("Обучение"),
                "Archived category 'Обучение' should be included in stat. Found categories: " + categoryNames
        );
    }

    private final SpendDbClient spendDbClient = new SpendDbClient();

    @Test
    @ApiLoginRest(username = "duck", password = "12345")
    void shouldIncludeArchivedCategoriesInStat123(@Token String bearerToken) {
        Optional<CategoryJson> category = spendDbClient.findCategoryByUsernameAndName("duck", "Обучение");

        Assertions.assertTrue(category.get().archived());

        ApolloCall<StatQuery.Data> call = apolloClient.query(
                StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(CurrencyValues.RUB)
                        .filterPeriod(null)
                        .build()
        ).addHttpHeader("authorization", bearerToken);

        ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(call).blockingGet();
        StatQuery.Data data = response.dataOrThrow();

        List<String> statCategoryNames = data.stat.statByCategories.stream()
                .map(stat -> stat.categoryName)
                .collect(Collectors.toList());

        Assertions.assertTrue(statCategoryNames.contains("Обучение"));
    }

}