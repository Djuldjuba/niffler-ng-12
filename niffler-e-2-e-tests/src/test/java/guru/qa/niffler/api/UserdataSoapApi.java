package guru.qa.niffler.api;

import jaxb.userdata.AcceptInvitationRequest;
import jaxb.userdata.AllUsersRequest;
import jaxb.userdata.CurrentUserRequest;
import jaxb.userdata.DeclineInvitationRequest;
import jaxb.userdata.FriendsPageRequest;
import jaxb.userdata.FriendsRequest;
import jaxb.userdata.RemoveFriendRequest;
import jaxb.userdata.SendInvitationRequest;
import jaxb.userdata.UserResponse;
import jaxb.userdata.UsersResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UserdataSoapApi {

    @Headers(value = {
            "Content-Type: text/xml; charset=utf-8",
            "Accept: text/xml, application/xml"
    })
    @POST("ws")
    Call<UserResponse> currentUser(@Body CurrentUserRequest request);

    @Headers(value = {
            "Content-Type: text/xml; charset=utf-8",
            "Accept: text/xml, application/xml"
    })
    @POST("ws")
    Call<UsersResponse> allUsers(@Body AllUsersRequest request);

    @Headers(value = {
            "Content-Type: text/xml; charset=utf-8",
            "Accept: text/xml, application/xml"
    })
    @POST("ws")
    Call<UsersResponse> friends(@Body FriendsRequest request);

    // НОВЫЙ МЕТОД для пагинации друзей
    @Headers(value = {
            "Content-Type: text/xml; charset=utf-8",
            "Accept: text/xml, application/xml"
    })
    @POST("ws")
    Call<UsersResponse> friendsPage(@Body FriendsPageRequest request);

    @Headers(value = {
            "Content-Type: text/xml; charset=utf-8",
            "Accept: text/xml, application/xml"
    })
    @POST("ws")
    Call<UserResponse> sendInvitation(@Body SendInvitationRequest request);

    @Headers(value = {
            "Content-Type: text/xml; charset=utf-8",
            "Accept: text/xml, application/xml"
    })
    @POST("ws")
    Call<UserResponse> acceptInvitation(@Body AcceptInvitationRequest request);

    @Headers(value = {
            "Content-Type: text/xml; charset=utf-8",
            "Accept: text/xml, application/xml"
    })
    @POST("ws")
    Call<UserResponse> declineInvitation(@Body DeclineInvitationRequest request);

    @Headers(value = {
            "Content-Type: text/xml; charset=utf-8",
            "Accept: text/xml, application/xml"
    })
    @POST("ws")
    Call<Void> removeFriend(@Body RemoveFriendRequest request);
}