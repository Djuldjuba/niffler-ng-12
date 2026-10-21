package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.niffler.FriendsWithCategoriesQuery;
import guru.qa.niffler.jupiter.annotation.ApiLoginRest;
import guru.qa.niffler.jupiter.annotation.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SecurityGraphQlTest extends BaseGraphQlTest {

    @Test
    @ApiLoginRest(username = "duck", password = "12345")
    void shouldNotQueryCategoriesForAnotherUser(@Token String bearerToken) {
        ApolloCall<FriendsWithCategoriesQuery.Data> call = apolloClient.query(new FriendsWithCategoriesQuery())
                .addHttpHeader("authorization", bearerToken);

        ApolloResponse<FriendsWithCategoriesQuery.Data> response = Rx2Apollo.single(call).blockingGet();
        Assertions.assertNotNull(response.errors);
        String errorMessage = response.errors.get(0).getMessage();
        Assertions.assertEquals("Can`t query categories for another user", errorMessage);
    }
}