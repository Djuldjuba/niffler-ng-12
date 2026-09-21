package guru.qa.niffler.service.api;

import guru.qa.niffler.api.UserdataSoapApi;
import guru.qa.niffler.api.core.converter.SoapConverterFactory;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.UserdataUserJson;
import guru.qa.niffler.service.UsersClient;
import io.qameta.allure.Step;
import jaxb.userdata.AcceptInvitationRequest;
import jaxb.userdata.AllUsersRequest;
import jaxb.userdata.Currency;
import jaxb.userdata.CurrentUserRequest;
import jaxb.userdata.DeclineInvitationRequest;
import jaxb.userdata.FriendsPageRequest;
import jaxb.userdata.FriendsRequest;
import jaxb.userdata.RemoveFriendRequest;
import jaxb.userdata.SendInvitationRequest;
import jaxb.userdata.UserResponse;
import jaxb.userdata.UsersResponse;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserdataSoapClient extends RestClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private final UserdataSoapApi userdataSoapApi;

    public UserdataSoapClient() {
        super(CFG.userdataUrl(), false,
                SoapConverterFactory.create("niffler-userdata"),
                HttpLoggingInterceptor.Level.BODY);
        this.userdataSoapApi = create(UserdataSoapApi.class);
    }

    @Step("Get current user SOAP")
    public UserResponse currentUser(CurrentUserRequest request) throws IOException {
        Response<UserResponse> response = userdataSoapApi.currentUser(request).execute();
        validateResponse(response);
        return response.body();
    }

    @Step("Get all users SOAP")
    public UsersResponse allUsers(AllUsersRequest request) throws IOException {
        Response<UsersResponse> response = userdataSoapApi.allUsers(request).execute();
        validateResponse(response);
        return response.body();
    }

    @Step("Get friends SOAP")
    public UsersResponse friends(FriendsRequest request) throws IOException {
        Response<UsersResponse> response = userdataSoapApi.friends(request).execute();
        validateResponse(response);
        return response.body();
    }

    @Step("Get friends with pagination SOAP")
    public UsersResponse friendsPage(FriendsPageRequest request) throws IOException {
        Response<UsersResponse> response = userdataSoapApi.friendsPage(request).execute();
        validateResponse(response);
        return response.body();
    }

    @Step("Send friend invitation SOAP")
    public UserResponse sendInvitation(SendInvitationRequest request) throws IOException {
        Response<UserResponse> response = userdataSoapApi.sendInvitation(request).execute();
        validateResponse(response);
        return response.body();
    }

    @Step("Accept friend invitation SOAP")
    public UserResponse acceptInvitation(AcceptInvitationRequest request) throws IOException {
        Response<UserResponse> response = userdataSoapApi.acceptInvitation(request).execute();
        validateResponse(response);
        return response.body();
    }

    @Step("Decline friend invitation SOAP")
    public UserResponse declineInvitation(DeclineInvitationRequest request) throws IOException {
        Response<UserResponse> response = userdataSoapApi.declineInvitation(request).execute();
        validateResponse(response);
        return response.body();
    }

    @Step("Remove friend via SOAP")
    public void removeFriend(RemoveFriendRequest request) throws IOException {
        Response<Void> response = userdataSoapApi.removeFriend(request).execute();
        if (!response.isSuccessful()) {
            throw new RuntimeException("Remove friend failed: " + response.code() + " - " +
                    (response.errorBody() != null ? response.errorBody().string() : ""));
        }
    }

    private <T> void validateResponse(Response<T> response) throws IOException {
        if (!response.isSuccessful()) {
            throw new RuntimeException("SOAP request failed: " + response.code() + " - " +
                    (response.errorBody() != null ? response.errorBody().string() : ""));
        }
        if (response.body() == null) {
            throw new RuntimeException("SOAP response body is null");
        }
    }

    @Override
    public UserdataUserJson createUser(String username, String password) {
        throw new UnsupportedOperationException("Not implemented for SOAP client");
    }

    @Override
    public UserJson createUserWithAuthorities(UserJson user) {
        throw new UnsupportedOperationException("Not implemented for SOAP client");
    }

    @Override
    public Optional<UserdataUserJson> findUserById(UUID id) {
        throw new UnsupportedOperationException("Not implemented for SOAP client");
    }

    @Override
    public Optional<UserdataUserJson> findUserByUsername(String username) {
        throw new UnsupportedOperationException("Not implemented for SOAP client");
    }

    @Override
    public void createIncomeInvitations(UserdataUserJson targetUser, int count) {
        throw new UnsupportedOperationException("Not implemented for SOAP client");
    }

    @Override
    public void createOutcomeInvitations(UserdataUserJson targetUser, int count) {
        throw new UnsupportedOperationException("Not implemented for SOAP client");
    }

    @Override
    public void createFriends(UserdataUserJson targetUser, int count) {
        throw new UnsupportedOperationException("Not implemented for SOAP client");
    }

    @Override
    public UserdataUserJson updateUser(UserdataUserJson user) {
        throw new UnsupportedOperationException("Not implemented for SOAP client");
    }

    @Override
    public void deleteUser(UserdataUserJson user) {
        throw new UnsupportedOperationException("Not implemented for SOAP client");
    }

    @Override
    public List<UserdataUserJson> allUsers(String username, String searchQuery) {
        AllUsersRequest request = new AllUsersRequest();
        request.setUsername(username);
        request.setSearchQuery(searchQuery);
        try {
            UsersResponse response = allUsers(request);
            return response.getUser().stream()
                    .map(this::toUserdataUserJson)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to get all users", e);
        }
    }

    @Override
    public List<UserdataUserJson> friends(String username, String searchQuery) {
        FriendsRequest request = new FriendsRequest();
        request.setUsername(username);
        request.setSearchQuery(searchQuery);
        try {
            UsersResponse response = friends(request);
            return response.getUser().stream()
                    .map(this::toUserdataUserJson)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to get friends", e);
        }
    }

    private UserdataUserJson toUserdataUserJson(jaxb.userdata.User user) {
        return new UserdataUserJson(
                UUID.fromString(user.getId()),
                user.getUsername(),
                user.getFirstname(),
                user.getSurname(),
                user.getFullname(),
                convertCurrency(user.getCurrency()),
                user.getPhoto(),
                user.getPhotoSmall(),
                user.getFriendshipStatus() != null ? user.getFriendshipStatus().name() : null
        );
    }

    private guru.qa.niffler.model.CurrencyValues convertCurrency(Currency currency) {
        if (currency == null) {
            return guru.qa.niffler.model.CurrencyValues.RUB;
        }
        return guru.qa.niffler.model.CurrencyValues.valueOf(currency.name());
    }
}