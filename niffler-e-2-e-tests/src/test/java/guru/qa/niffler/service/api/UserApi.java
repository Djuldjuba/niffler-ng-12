package guru.qa.niffler.service.api;

import guru.qa.niffler.model.UserdataUserJson;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface UserApi {

    @GET("/internal/users/all")
    Call<List<UserdataUserJson>> getAllUsers(
            @Query("username") String username,
            @Query(value = "searchQuery", encoded = true) String searchQuery
    );
}
