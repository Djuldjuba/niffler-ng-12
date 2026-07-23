package guru.qa.niffler.api;

import guru.qa.niffler.model.UserdataUserJson;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface UsersApi {

    @GET("/internal/users/all")
    Call<List<UserdataUserJson>> allUsers(
            @Query("username") String username,
            @Query("searchQuery") String searchQuery
    );

    @GET("/internal/friends/all")
    Call<List<UserdataUserJson>> friends(
            @Query("username") String username,
            @Query("searchQuery") String searchQuery
    );
}