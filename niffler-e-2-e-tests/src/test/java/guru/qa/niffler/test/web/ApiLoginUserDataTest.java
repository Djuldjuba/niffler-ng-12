package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.TestMethodContextExtension;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@ExtendWith({BrowserExtension.class, TestMethodContextExtension.class})
public class ApiLoginUserDataTest {

    @Test
    @ApiLogin(username = "duck", password = "12345")
    void shouldLoadUserDataForExistingUser(UserJson user) {
        TestData testData = user.testData();

        List<String> categories = testData.getCategories();
        Assertions.assertNotNull(categories);

        List<SpendJson> spends = testData.getSpends();
        Assertions.assertNotNull(spends);
    }
}