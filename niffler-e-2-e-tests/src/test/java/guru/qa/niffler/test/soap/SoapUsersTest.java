package guru.qa.niffler.test.soap;

import guru.qa.niffler.jupiter.annotation.SoapTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.TestMethodContextExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.api.UserdataSoapClient;
import jaxb.userdata.CurrentUserRequest;
import jaxb.userdata.UserResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

@SoapTest
@ExtendWith(TestMethodContextExtension.class)
public class SoapUsersTest {

    private final UserdataSoapClient userdataSoapClient = new UserdataSoapClient();
    @Test
    @User
    void currentUserTest(UserJson user) throws IOException {
        CurrentUserRequest request = new CurrentUserRequest();
        request.setUsername(user.username());
        UserResponse response = userdataSoapClient.currentUser(request);
        Assertions.assertEquals(
                user.username(),
                response.getUser().getUsername()
        );
    }
}
