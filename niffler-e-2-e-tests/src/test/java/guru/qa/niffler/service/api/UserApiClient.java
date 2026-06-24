package guru.qa.niffler.service.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserdataUserJson;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;

public class UserApiClient {

    private static final Config CFG = Config.getInstance();

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.userdataUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final UserApi userApi = retrofit.create(UserApi.class);

    public List<UserdataUserJson> getAllUsers(String username, String searchQuery) {
        try {
            var response = userApi.getAllUsers(username, searchQuery).execute();
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
