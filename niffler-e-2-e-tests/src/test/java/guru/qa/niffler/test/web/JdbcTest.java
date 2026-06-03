package guru.qa.niffler.test.web;

import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.model.*;
import guru.qa.niffler.service.SpendDbClient;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;

import static guru.qa.niffler.data.IsolationLevels.READ_UNCOMMITTED;

public class JdbcTest {

    @Test
    void txTest() {
        SpendDbClient spendDbClient = new SpendDbClient();

        SpendJson spend = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "cat-name-tx",
                                "duck",
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "spend-name-tx",
                        "duck"
                ),
                READ_UNCOMMITTED
        );

        System.out.println(spend);
    }

    @Test
    void createUserWithAuthoritiesTest() {
        SpendDbClient spendDbClient = new SpendDbClient();

        UserJson user = spendDbClient.createUserWithAuthorities(
                new UserJson(
                        null,
                        "testuser_with_auth555",
                        "encoded_password_555",
                        true,
                        true,
                        true,
                        true
                ),
                Arrays.asList(
                        new AuthorityJson(null, Authority.read, null),
                        new AuthorityJson(null, Authority.write, null)
                ),
                READ_UNCOMMITTED
        );

        System.out.println("Created user: " + user);
    }

}
