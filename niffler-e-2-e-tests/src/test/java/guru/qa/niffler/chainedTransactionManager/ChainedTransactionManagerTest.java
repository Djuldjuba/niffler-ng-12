package guru.qa.niffler.chainedTransactionManager;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserdataUserJson;
import org.junit.jupiter.api.Test;

public class ChainedTransactionManagerTest {

    @Test
    void createUserWithChainedTransactionTest() {
        UsersDbClientChained userDbClient = new UsersDbClientChained();

        UserdataUserJson user = userDbClient.createUser(
                new UserdataUserJson(
                        null,
                        "chained_error_" + System.currentTimeMillis(),
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
