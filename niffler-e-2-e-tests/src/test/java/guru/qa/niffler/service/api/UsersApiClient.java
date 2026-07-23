package guru.qa.niffler.service.api;

import guru.qa.niffler.api.UsersApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.UserdataUserJson;
import guru.qa.niffler.service.UsersClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UsersApiClient implements UsersClient {

    private static final Config CFG = Config.getInstance();

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.userdataUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final UsersApi usersApi = retrofit.create(UsersApi.class);

    @Override
    public UserdataUserJson createUser(String username, String password) {
        return null;
    }

    @Override
    public UserJson createUserWithAuthorities(UserJson user) {
        return null;
    }

    @Override
    public Optional<UserdataUserJson> findUserById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<UserdataUserJson> findUserByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public void createIncomeInvitations(UserdataUserJson targetUser, int count) {

    }

    @Override
    public void createOutcomeInvitations(UserdataUserJson targetUser, int count) {

    }

    @Override
    public void createFriends(UserdataUserJson targetUser, int count) {

    }

    @Override
    public UserdataUserJson updateUser(UserdataUserJson user) {
        return null;
    }

    @Override
    public void deleteUser(UserdataUserJson user) {

    }

    @Override
    public List<UserdataUserJson> allUsers(String username, String searchQuery) {
        try {
            var response = usersApi.allUsers(username, searchQuery).execute();

            if (response.isSuccessful() && response.body() != null) {
                System.out.println("[UsersApiClient] allUsers: " + response.body().size() + " users for " + username);
                return response.body();
            }

            if (!response.isSuccessful()) {
                System.err.println("[UsersApiClient] allUsers error: " + response.code() + " - " + response.message());
                if (response.errorBody() != null) {
                    System.err.println("[UsersApiClient] Error body: " + response.errorBody().string());
                }
            }

            return List.of();
        } catch (IOException e) {
            System.err.println("[UsersApiClient] allUsers failed for: " + username);
            throw new RuntimeException("Failed to get all users for: " + username, e);
        }
    }

    @Override
    public List<UserdataUserJson> friends(String username, String searchQuery) {
        try {
            var response = usersApi.friends(username, searchQuery).execute();

            if (response.isSuccessful() && response.body() != null) {
                System.out.println("[UsersApiClient] friends: " + response.body().size() + " friends for " + username);
                response.body().forEach(friend ->
                        System.out.println("  - " + friend.username() + " (" + friend.friendshipStatus() + ")")
                );
                return response.body();
            }

            if (!response.isSuccessful()) {
                System.err.println("[UsersApiClient] friends error: " + response.code() + " - " + response.message());
                if (response.errorBody() != null) {
                    System.err.println("[UsersApiClient] Error body: " + response.errorBody().string());
                }
            }

            return List.of();
        } catch (IOException e) {
            System.err.println("[UsersApiClient] friends failed for: " + username);
            throw new RuntimeException("Failed to get friends for: " + username, e);
        }
    }
}