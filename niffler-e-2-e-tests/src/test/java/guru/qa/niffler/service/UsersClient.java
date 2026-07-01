package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.UserdataUserJson;

import java.util.Optional;
import java.util.UUID;

public interface UsersClient {

    UserdataUserJson createUser(String username, String password);

    UserJson createUserWithAuthorities(UserJson user);

    Optional<UserdataUserJson> findUserById(UUID id);

    Optional<UserdataUserJson> findUserByUsername(String username);

    void createIncomeInvitations(UserdataUserJson targetUser, int count);

    void createOutcomeInvitations(UserdataUserJson targetUser, int count);

    void createFriends(UserdataUserJson targetUser, int count);

    UserdataUserJson updateUser(UserdataUserJson user);

    void deleteUser(UserdataUserJson user);
}
