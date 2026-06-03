package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.*;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.AuthorityJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.UserdataUserJson;

import java.util.List;

public class UserdataDbClient {

    private static final Config CFG = Config.getInstance();

    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryHibernate();
    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.userdataJdbcUrl(),
            CFG.authJdbcUrl()
    );

    public UserdataUserJson createUser(UserdataUserJson user) {
        return xaTransactionTemplate.execute(() -> {
            UserEntity userEntity = UserEntity.fromJson(user);
            UserEntity createdUser = userdataUserRepository.create(userEntity);
            return UserdataUserJson.fromEntity(createdUser);
        });
    }

    public UserJson createUserWithAuthorities(UserJson user, List<AuthorityJson> authorities) {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity userEntity = AuthUserEntity.fromJson(user);

            for (AuthorityJson authorityJson : authorities) {
                AuthorityEntity authorityEntity = AuthorityEntity.fromJson(authorityJson);
                authorityEntity.setUser(userEntity);
                userEntity.getAuthorities().add(authorityEntity);
            }

            AuthUserEntity createdUser = authUserRepository.create(userEntity);
            return UserJson.fromEntity(createdUser);
        });
    }
}
