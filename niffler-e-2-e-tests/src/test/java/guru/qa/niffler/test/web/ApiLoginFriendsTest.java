package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.TestMethodContextExtension;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.UserdataUserJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@ExtendWith({BrowserExtension.class, TestMethodContextExtension.class})
public class ApiLoginFriendsTest {

    @Test
    @ApiLogin(username = "alex", password = "12345")
    void shouldLoadFriendsAndInvitations(UserJson user) {
        TestData testData = user.testData();

        List<UserdataUserJson> friends = testData.getFriends();
        Assertions.assertEquals(1, friends.size());

        UserdataUserJson friend = friends.get(0);
        Assertions.assertEquals("duck", friend.username());

        List<UserdataUserJson> invitations = testData.getInvitations();
        Assertions.assertEquals(1, invitations.size());

        UserdataUserJson invitation = invitations.get(0);
        Assertions.assertEquals("bee", invitation.username());
        Assertions.assertEquals("INVITE_RECEIVED", invitation.friendshipStatus());
    }
}