package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.niffler.Friends2SubQueriesQuery;
import guru.qa.niffler.jupiter.annotation.ApiLoginRest;
import guru.qa.niffler.jupiter.annotation.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DepthLimitGraphQlTest extends BaseGraphQlTest {

    @Test
    @ApiLoginRest(username = "duck", password = "12345")
    void shouldThrowExceptionWhenQueryExceedsDepthLimit(@Token String bearerToken) {
        ApolloCall<Friends2SubQueriesQuery.Data> call = apolloClient.query(new Friends2SubQueriesQuery())
                .addHttpHeader("authorization", bearerToken);

        ApolloResponse<Friends2SubQueriesQuery.Data> response = Rx2Apollo.single(call).blockingGet();

        Assertions.assertNotNull(response.errors);

        String errorMessage = response.errors.get(0).getMessage();
        Assertions.assertEquals("Can`t fetch over 2 friends sub-queries", errorMessage);
    }
}