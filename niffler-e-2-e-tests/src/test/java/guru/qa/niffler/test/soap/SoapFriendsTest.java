package guru.qa.niffler.test.soap;

import guru.qa.niffler.jupiter.annotation.SoapTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.TestMethodContextExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.api.UserdataSoapClient;
import guru.qa.niffler.service.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import jaxb.userdata.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SoapTest
@ExtendWith(TestMethodContextExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SoapFriendsTest {

    private UserdataSoapClient soapClient;
    private UsersDbClient usersDbClient;
    private static final String PASSWORD = "12345";

    @BeforeEach
    void setUp() {
        soapClient = new UserdataSoapClient();
        usersDbClient = new UsersDbClient();
    }

    @Test
    @Order(1)
    @User(friends = 3)
    void friendsShouldBeReturnedAsPageWithPagination(UserJson user) throws IOException {
        FriendsPageRequest requestPage0 = new FriendsPageRequest();
        requestPage0.setUsername(user.username());
        PageInfo pageInfo0 = new PageInfo();
        pageInfo0.setPage(0);
        pageInfo0.setSize(2);
        requestPage0.setPageInfo(pageInfo0);

        UsersResponse responsePage0 = soapClient.friendsPage(requestPage0);
        assertEquals(2, responsePage0.getUser().size());
        assertEquals(0, responsePage0.getNumber().intValue());
        assertEquals(3, responsePage0.getTotalElements().longValue());

        FriendsPageRequest requestPage1 = new FriendsPageRequest();
        requestPage1.setUsername(user.username());
        PageInfo pageInfo1 = new PageInfo();
        pageInfo1.setPage(1);
        pageInfo1.setSize(2);
        requestPage1.setPageInfo(pageInfo1);

        UsersResponse responsePage1 = soapClient.friendsPage(requestPage1);
        assertEquals(1, responsePage1.getUser().size());
        assertEquals(1, responsePage1.getNumber().intValue());

        List<String> page0Usernames = responsePage0.getUser().stream()
                .map(jaxb.userdata.User::getUsername)
                .collect(Collectors.toList());
        List<String> page1Usernames = responsePage1.getUser().stream()
                .map(jaxb.userdata.User::getUsername)
                .collect(Collectors.toList());

        assertTrue(page0Usernames.stream().noneMatch(page1Usernames::contains));
    }

    @Test
    @Order(2)
    @User(friends = 3)
    void friendsShouldBeFilteredByUsernameWhenSearchQueryProvided(UserJson user) throws IOException {
        FriendsRequest allRequest = new FriendsRequest();
        allRequest.setUsername(user.username());
        UsersResponse allResponse = soapClient.friends(allRequest);

        String firstFriend = allResponse.getUser().get(0).getUsername();
        String searchQuery = firstFriend.split("_")[0];

        FriendsRequest request = new FriendsRequest();
        request.setUsername(user.username());
        request.setSearchQuery(searchQuery);

        UsersResponse response = soapClient.friends(request);
        assertTrue(response.getUser().stream()
                .allMatch(u -> u.getUsername().contains(searchQuery)));
    }

    @Test
    @Order(3)
    @User(friends = 1)
    void friendshipShouldBeRemoved(UserJson user) throws IOException {
        FriendsRequest friendsRequest = new FriendsRequest();
        friendsRequest.setUsername(user.username());
        UsersResponse before = soapClient.friends(friendsRequest);

        String friendToDelete = before.getUser().get(0).getUsername();

        assertEquals(1, before.getUser().size());
        assertEquals(FriendshipStatus.FRIEND, before.getUser().get(0).getFriendshipStatus());

        RemoveFriendRequest removeRequest = new RemoveFriendRequest();
        removeRequest.setUsername(user.username());
        removeRequest.setFriendToBeRemoved(friendToDelete);
        soapClient.removeFriend(removeRequest);

        UsersResponse after = soapClient.friends(friendsRequest);
        assertEquals(0, after.getUser().size());
    }

    @Test
    @Order(4)
    @User
    void acceptInvitationShouldMakeUsersFriends(UserJson user) throws IOException {
        String requester = RandomDataUtils.randomUsername() + "_requester";
        usersDbClient.createUser(requester, PASSWORD);

        SendInvitationRequest inviteRequest = new SendInvitationRequest();
        inviteRequest.setUsername(requester);
        inviteRequest.setFriendToBeRequested(user.username());
        soapClient.sendInvitation(inviteRequest);

        FriendsRequest friendsRequest = new FriendsRequest();
        friendsRequest.setUsername(user.username());
        UsersResponse before = soapClient.friends(friendsRequest);
        assertEquals(FriendshipStatus.INVITE_RECEIVED, before.getUser().get(0).getFriendshipStatus());

        AcceptInvitationRequest acceptRequest = new AcceptInvitationRequest();
        acceptRequest.setUsername(user.username());
        acceptRequest.setFriendToBeAdded(requester);
        soapClient.acceptInvitation(acceptRequest);

        UsersResponse after = soapClient.friends(friendsRequest);
        assertEquals(FriendshipStatus.FRIEND, after.getUser().get(0).getFriendshipStatus());
    }

    @Test
    @Order(5)
    @User
    void declineInvitationShouldRemoveRequest(UserJson user) throws IOException {
        String requester = RandomDataUtils.randomUsername() + "_requester";
        usersDbClient.createUser(requester, PASSWORD);

        SendInvitationRequest inviteRequest = new SendInvitationRequest();
        inviteRequest.setUsername(requester);
        inviteRequest.setFriendToBeRequested(user.username());
        soapClient.sendInvitation(inviteRequest);

        FriendsRequest friendsRequest = new FriendsRequest();
        friendsRequest.setUsername(user.username());
        UsersResponse before = soapClient.friends(friendsRequest);
        assertEquals(FriendshipStatus.INVITE_RECEIVED, before.getUser().get(0).getFriendshipStatus());

        DeclineInvitationRequest declineRequest = new DeclineInvitationRequest();
        declineRequest.setUsername(user.username());
        declineRequest.setInvitationToBeDeclined(requester);
        soapClient.declineInvitation(declineRequest);

        UsersResponse after = soapClient.friends(friendsRequest);
        assertEquals(0, after.getUser().size());
    }

    @Test
    @Order(6)
    @User
    void sendInvitationShouldCreateFriendshipRequest(UserJson user) throws IOException {
        String target = RandomDataUtils.randomUsername() + "_target";
        usersDbClient.createUser(target, PASSWORD);

        SendInvitationRequest request = new SendInvitationRequest();
        request.setUsername(user.username());
        request.setFriendToBeRequested(target);

        UserResponse response = soapClient.sendInvitation(request);
        assertEquals(FriendshipStatus.INVITE_SENT, response.getUser().getFriendshipStatus());

        FriendsRequest targetRequest = new FriendsRequest();
        targetRequest.setUsername(target);
        UsersResponse targetResponse = soapClient.friends(targetRequest);
        assertEquals(FriendshipStatus.INVITE_RECEIVED, targetResponse.getUser().get(0).getFriendshipStatus());
    }
}