package guru.qa.niffler.test.gql;

import com.apollographql.java.client.ApolloClient;
import guru.qa.niffler.config.Config;
import com.apollographql.adapter.core.DateAdapter;
import guru.qa.niffler.jupiter.annotation.GqlTest;
import guru.qa.niffler.type.Date;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

@GqlTest
public class BaseGraphQlTest {

    protected static final Config CFG = Config.getInstance();

    private static final Interceptor BEARER_AUTH_INTERCEPTOR = chain -> {
        Request originalRequest = chain.request();
        String authHeader = originalRequest.header("authorization");

        if (authHeader != null && !authHeader.startsWith("Bearer ")) {
            Request newRequest = originalRequest.newBuilder()
                    .header("authorization", "Bearer " + authHeader)
                    .build();
            return chain.proceed(newRequest);
        }

        return chain.proceed(originalRequest);
    };

    protected static final ApolloClient apolloClient = new ApolloClient.Builder()
            .serverUrl(CFG.gatewayUrl() + "graphql")
            .addCustomScalarAdapter(Date.type, DateAdapter.INSTANCE)
            .okHttpClient(
                    new OkHttpClient.Builder()
                            .addNetworkInterceptor(new AllureOkHttp3())
                            .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                            .addNetworkInterceptor(BEARER_AUTH_INTERCEPTOR)
                            .build()
            ).build();
}