package guru.qa.niffler.test.grpc;

import guru.qa.niffler.grpc.*;
import guru.qa.niffler.jupiter.annotation.GrpcTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@GrpcTest
public class UserdataGrpcTests extends BaseUserdataGrpcTest {

    private String username;

    @BeforeEach
    void setUp() {
        username = "testuser" + System.currentTimeMillis();
        createUser(username);
        createUser("friend1");
        createUser("friend2");
        createUser("friend3");
        createFriend(username, "friend1");
        createFriend(username, "friend2");
        createFriend(username, "friend3");
    }

    private void createUser(String name) {
        blockingStub.updateUser(UpdateUserRequest.newBuilder()
                .setUser(User.newBuilder().setUsername(name).build())
                .build());
    }

    private void createFriend(String user, String friend) {
        blockingStub.sendInvitation(SendInvitationRequest.newBuilder()
                .setUsername(user).setFriendToBeRequested(friend).build());
        blockingStub.acceptInvitation(AcceptInvitationRequest.newBuilder()
                .setUsername(friend).setFriendToBeAdded(user).build());
    }

    @Test
    void friendsShouldBeReturnedAsPageWithPagination() {
        UsersPageResponse page0 = blockingStub.getFriendsV2(FriendsPageRequest.newBuilder()
                .setUsername(username)
                .setPageInfo(PageInfo.newBuilder().setPage(0).setSize(1).build())
                .build());

        assertEquals(3, page0.getTotalElements());
        assertEquals(3, page0.getTotalPages());
        assertTrue(page0.getFirst());
        assertFalse(page0.getLast());
        assertEquals(1, page0.getEdgesList().size());

        UsersPageResponse page1 = blockingStub.getFriendsV2(FriendsPageRequest.newBuilder()
                .setUsername(username)
                .setPageInfo(PageInfo.newBuilder().setPage(1).setSize(1).build())
                .build());

        assertFalse(page1.getFirst());
        assertFalse(page1.getLast());
        assertEquals(1, page1.getEdgesList().size());
        assertEquals(1, page1.getNumber());
    }

    @Test
    void friendsShouldBeFilteredByUsernameWhenSearchQueryProvided() {
        UsersPageResponse response = blockingStub.getFriends(FriendsRequest.newBuilder()
                .setUsername(username)
                .setSearchQuery("friend1")
                .build());

        assertEquals(1, response.getEdgesList().size());
        assertEquals("friend1", response.getEdgesList().get(0).getUsername());
    }

    @Test
    void friendshipShouldBeRemoved() {
        String friendToDelete = "friendToDelete";
        createUser(friendToDelete);
        createFriend(username, friendToDelete);

        UsersPageResponse before = blockingStub.getFriends(FriendsRequest.newBuilder()
                .setUsername(username)
                .build());
        assertEquals(4, before.getEdgesList().size());

        blockingStub.removeFriend(RemoveFriendRequest.newBuilder()
                .setUsername(username)
                .setFriendToBeRemoved(friendToDelete)
                .build());

        UsersPageResponse after = blockingStub.getFriends(FriendsRequest.newBuilder()
                .setUsername(username)
                .build());
        assertEquals(3, after.getEdgesList().size());
        assertTrue(after.getEdgesList().stream().noneMatch(u -> u.getUsername().equals(friendToDelete)));
    }

    @Test
    void acceptInvitationShouldMakeUsersFriends() {
        String requester = "requester" + System.currentTimeMillis();
        createUser(requester);

        blockingStub.sendInvitation(SendInvitationRequest.newBuilder()
                .setUsername(requester)
                .setFriendToBeRequested(username)
                .build());

        blockingStub.acceptInvitation(AcceptInvitationRequest.newBuilder()
                .setUsername(username)
                .setFriendToBeAdded(requester)
                .build());

        UsersPageResponse friendsOfUser = blockingStub.getFriends(FriendsRequest.newBuilder()
                .setUsername(username)
                .build());
        assertTrue(friendsOfUser.getEdgesList().stream().anyMatch(u -> u.getUsername().equals(requester)));

        UsersPageResponse friendsOfRequester = blockingStub.getFriends(FriendsRequest.newBuilder()
                .setUsername(requester)
                .build());
        assertTrue(friendsOfRequester.getEdgesList().stream().anyMatch(u -> u.getUsername().equals(username)));
    }

    @Test
    void declineInvitationShouldRemoveRequest() {
        String requester = "requester" + System.currentTimeMillis();
        createUser(requester);

        blockingStub.sendInvitation(SendInvitationRequest.newBuilder()
                .setUsername(requester)
                .setFriendToBeRequested(username)
                .build());

        blockingStub.declineInvitation(DeclineInvitationRequest.newBuilder()
                .setUsername(username)
                .setInvitationToBeDeclined(requester)
                .build());

        UsersPageResponse friendsOfUser = blockingStub.getFriends(FriendsRequest.newBuilder()
                .setUsername(username)
                .build());
        assertTrue(friendsOfUser.getEdgesList().stream().noneMatch(u -> u.getUsername().equals(requester)));

        UsersPageResponse friendsOfRequester = blockingStub.getFriends(FriendsRequest.newBuilder()
                .setUsername(requester)
                .build());
        assertTrue(friendsOfRequester.getEdgesList().stream().noneMatch(u -> u.getUsername().equals(username)));
    }

    @Test
    void sendInvitationShouldCreateFriendshipRequest() {
        String target = "target" + System.currentTimeMillis();
        createUser(target);

        UserResponse response = blockingStub.sendInvitation(SendInvitationRequest.newBuilder()
                .setUsername(username)
                .setFriendToBeRequested(target)
                .build());

        assertEquals(target, response.getUser().getUsername());
        assertEquals(FriendshipStatus.INVITE_SENT, response.getUser().getFriendshipStatus());

        UsersPageResponse friendsOfUser = blockingStub.getFriends(FriendsRequest.newBuilder()
                .setUsername(username)
                .build());
        assertFalse(friendsOfUser.getEdgesList().stream().anyMatch(u -> u.getUsername().equals(target)));
    }
}