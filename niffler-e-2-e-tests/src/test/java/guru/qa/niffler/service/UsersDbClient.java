package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.UserdataUserJson;
import guru.qa.niffler.utils.RandomDataUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    @Override
    public UserdataUserJson createUser(String username, String password) {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUser = createAuthUserEntity(username, password);
            authUserRepository.create(authUser);

            UserEntity userEntity = createUserEntity(username);
            UserEntity createdUser = userdataUserRepository.create(userEntity);

            return UserdataUserJson.fromEntity(createdUser);
        });
    }

    @Override
    public UserJson createUserWithAuthorities(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUserEntity = AuthUserEntity.fromJson(user);

            authUserEntity.setAuthorities(
                    Arrays.stream(Authority.values()).map(
                            authority -> {
                                AuthorityEntity ae = new AuthorityEntity();
                                ae.setUser(authUserEntity);
                                ae.setAuthority(authority);
                                return ae;
                            }).toList()
            );

            AuthUserEntity createdUser = authUserRepository.create(authUserEntity);
            return UserJson.fromEntity(createdUser);
        });
    }

    @Override
    public Optional<UserdataUserJson> findUserById(UUID id) {
        return userdataUserRepository.findById(id)
                .map(UserdataUserJson::fromEntity);
    }

    @Override
    public Optional<UserdataUserJson> findUserByUsername(String username) {
        return userdataUserRepository.findByUsername(username)
                .map(UserdataUserJson::fromEntity);
    }

    @Override
    public void createIncomeInvitations(UserdataUserJson targetUser, int count) {
        if (count <= 0) return;

        UserEntity targetEntity = userdataUserRepository.findById(targetUser.id())
                .orElseThrow(() -> new RuntimeException("Target user not found: " + targetUser.id()));

        for (int i = 0; i < count; i++) {
            xaTransactionTemplate.execute(() -> {
                String username = RandomDataUtils.randomUsername();
                AuthUserEntity authUser = createAuthUserEntity(username, "12345");
                authUserRepository.create(authUser);

                UserEntity addressee = userdataUserRepository.create(createUserEntity(username));
                userdataUserRepository.addIncomeInvitation(targetEntity, addressee);
                return null;
            });
        }
    }

    @Override
    public void createOutcomeInvitations(UserdataUserJson targetUser, int count) {
        if (count <= 0) return;

        UserEntity targetEntity = userdataUserRepository.findById(targetUser.id())
                .orElseThrow(() -> new RuntimeException("Target user not found: " + targetUser.id()));

        for (int i = 0; i < count; i++) {
            xaTransactionTemplate.execute(() -> {
                String username = RandomDataUtils.randomUsername();
                AuthUserEntity authUser = createAuthUserEntity(username, "12345");
                authUserRepository.create(authUser);

                UserEntity requester = userdataUserRepository.create(createUserEntity(username));
                userdataUserRepository.addOutcomeInvitation(requester, targetEntity);
                return null;
            });
        }
    }

    @Override
    public void createFriends(UserdataUserJson targetUser, int count) {
        if (count <= 0) return;

        UserEntity targetEntity = userdataUserRepository.findById(targetUser.id())
                .orElseThrow(() -> new RuntimeException("Target user not found: " + targetUser.id()));

        for (int i = 0; i < count; i++) {
            xaTransactionTemplate.execute(() -> {
                String username = RandomDataUtils.randomUsername();
                AuthUserEntity authUser = createAuthUserEntity(username, "12345");
                authUserRepository.create(authUser);

                UserEntity friend = userdataUserRepository.create(createUserEntity(username));
                userdataUserRepository.addFriend(targetEntity, friend);
                return null;
            });
        }
    }

    @Override
    public UserdataUserJson updateUser(UserdataUserJson user) {
        return xaTransactionTemplate.execute(() -> {
            UserEntity userEntity = UserEntity.fromJson(user);
            UserEntity updatedUser = userdataUserRepository.update(userEntity);
            return UserdataUserJson.fromEntity(updatedUser);
        });
    }

    @Override
    public void deleteUser(UserdataUserJson user) {
        xaTransactionTemplate.execute(() -> {
            UserEntity userEntity = userdataUserRepository.findById(user.id())
                    .orElseThrow(() -> new RuntimeException("User not found: " + user.id()));
            userdataUserRepository.remove(userEntity);
            return null;
        });
    }

    @Override
    public List<UserdataUserJson> allUsers(String username, String searchQuery) {
        return List.of();
    }

    @Override
    public List<UserdataUserJson> friends(String username, String searchQuery) {
        return List.of();
    }

    private AuthUserEntity createAuthUserEntity(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(password);
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
                Arrays.stream(Authority.values()).map(
                        authority -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setUser(authUser);
                            ae.setAuthority(authority);
                            return ae;
                        }).toList()
        );
        return authUser;
    }

    private UserEntity createUserEntity(String username) {
        UserEntity ue = new UserEntity();
        ue.setUsername(username);
        ue.setCurrency(CurrencyValues.RUB);
        return ue;
    }
}