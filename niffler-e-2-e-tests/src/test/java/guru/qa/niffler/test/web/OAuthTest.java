package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.TestMethodContextExtension;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestMethodContextExtension.class)
@ExtendWith(BrowserExtension.class)
public class OAuthTest {

    @Test
    @ApiLogin(username = "duck", password = "12345")
    void oauthTest(@Token String token, UserJson user) {
        System.out.println(user);
        Assertions.assertNotNull(token);
    }
}