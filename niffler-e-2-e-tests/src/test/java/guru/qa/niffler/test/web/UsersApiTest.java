package guru.qa.niffler.test.web;

import guru.qa.niffler.model.UserdataUserJson;
import guru.qa.niffler.service.api.UserApiClient;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
public class UsersApiTest {


    private final UserApiClient userApiClient = new UserApiClient();

    @Test
    @Order(1)
    void shouldReturnEmptyList() {
        List<UserdataUserJson> users = userApiClient.getAllUsers("duck", "nonexistent");
        assertNotNull(users);
        assertTrue(users.isEmpty(), "Список должен быть пустым");
    }

    @Test
    @Order(2)
    void shouldReturnNonEmptyList() {
        List<UserdataUserJson> users = userApiClient.getAllUsers("duck", "Michi");
        assertNotNull(users);
        assertFalse(users.isEmpty(), "Список не должен быть пустым");
        assertTrue(users.stream().anyMatch(u -> u.username().equals("Michi")));
    }
}
