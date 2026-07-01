package guru.qa.niffler.service.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserdataUserJson;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;

public class UserApiClient {

    private static final Config CFG = Config.getInstance();

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(
                    new AllureOkHttp3()
                            .setRequestTemplate("http-request.ftl")
                            .setResponseTemplate("http-response.ftl")
            )
            .addNetworkInterceptor(
                    new HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build();

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.userdataUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final UserApi userApi = retrofit.create(UserApi.class);

    public List<UserdataUserJson> getAllUsers(String username, String searchQuery) {
        try {
            Response<List<UserdataUserJson>> response = userApi.getAllUsers(username, searchQuery).execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                throw new RuntimeException("Failed to get users. Code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
