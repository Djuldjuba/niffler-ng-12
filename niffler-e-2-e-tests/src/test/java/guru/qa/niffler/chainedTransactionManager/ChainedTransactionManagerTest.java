package guru.qa.niffler.chainedTransactionManager;

import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.UserDataEntity;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserdataUserJson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class ChainedTransactionManagerTest {

    private final UsersDbClientChained userDbClient = new UsersDbClientChained();
    private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
    private final UserdataUserDao userdataDao = new UserdataUserDaoJdbc();

    private String testUsername;

    @AfterEach
    void cleanup() {
        if (testUsername != null) {
            userdataDao.findByUsername(testUsername).ifPresent(userdataDao::delete);
            authUserDao.findByUsername(testUsername).ifPresent(u -> {
            });
        }
    }

    @Test
    void createUserWithChainedTransactionTest() {
        testUsername = "chained_ok_" + System.currentTimeMillis();

        UserdataUserJson user = userDbClient.createUser(
                new UserdataUserJson(
                        null,
                        testUsername,
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null
                )
        );
        System.out.println("Created user: " + user);
    }

    @Test
    void chainedTxShouldRollbackBothWhenUserdataFailsAndAuthCommittedLastTest() {
        testUsername = "chained_rollback_" + System.currentTimeMillis();

        createConflictUserInUserdata(testUsername);

        boolean authBefore = authUserDao.findByUsername(testUsername).isPresent();
        boolean userdataBefore = userdataDao.findByUsername(testUsername).isPresent();

        Assertions.assertThrows(
                RuntimeException.class,
                () -> userDbClient.createUserAuthFirst(new UserdataUserJson(
                        null, testUsername, null, null, null,
                        CurrencyValues.RUB, null, null
                ))
        );

        boolean authAfter = authUserDao.findByUsername(testUsername).isPresent();
        boolean userdataAfter = userdataDao.findByUsername(testUsername).isPresent();

        System.out.printf("AUTH-FIRST: BEFORE auth=%s userdata=%s | AFTER auth=%s userdata=%s%n",
                authBefore, userdataBefore, authAfter, userdataAfter);

        Assertions.assertFalse(authAfter, "auth должен был откатиться");
        Assertions.assertTrue(userdataAfter, "userdata содержит только setup-запись");
        Assertions.assertEquals(authBefore, authAfter,
                "auth не должен был измениться при полном откате");
    }

    private void createConflictUserInUserdata(String username) {
        UserDataEntity conflict = new UserDataEntity();
        conflict.setId(UUID.randomUUID());
        conflict.setUsername(username);
        conflict.setCurrency(CurrencyValues.RUB);
        userdataDao.createUser(conflict);
    }
}