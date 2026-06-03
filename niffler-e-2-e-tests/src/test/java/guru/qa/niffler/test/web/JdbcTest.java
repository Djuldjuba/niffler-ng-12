package guru.qa.niffler.test.web;

import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.model.*;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserdataDbClient;
import guru.qa.niffler.service.UsersDbClient;
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
                                "проверка 6.1",
                                "duck",
                                false
                        ),
                        CurrencyValues.RUB,
                        3333.3,
                        "вот так вот",
                        "duck"
                ),
                READ_UNCOMMITTED
        );

        System.out.println(spend);
    }

    @Test
    void createUserWithAuthoritiesTest() {
        UserdataDbClient userdataDbClient = new UserdataDbClient();

        UserJson user = userdataDbClient.createUserWithAuthorities(
                new UserJson(
                        null,
                        "valentin-11",
                        "1231324",
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

    @Test
    void springJdbcTest() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserdataUserJson user = usersDbClient.createUser(
                new UserdataUserJson(
                        null,
                        "valentin-9",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null
                )
        );
        System.out.println(user);
    }
}
