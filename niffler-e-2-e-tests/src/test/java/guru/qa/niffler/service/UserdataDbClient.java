package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserDataEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.AuthorityJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.UserdataUserJson;

import java.util.List;

public class UserdataDbClient {

    private static final Config CFG = Config.getInstance();
    private final UserdataUserDao userDao = new UserdataUserDaoJdbc();

    private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    public UserdataUserJson createUser(UserdataUserJson user) {
        UserDataEntity userEntity = UserDataEntity.fromJson(user);

        return UserdataUserJson.fromEntity(
                userDao.createUser(userEntity)
        );
    }

    public UserJson createUserWithAuthorities(UserJson user, List<AuthorityJson> authorities, int isolationLevel) {
        return jdbcTxTemplate.execute(() -> {
                    AuthUserEntity userEntity = AuthUserEntity.fromJson(user);
                    AuthUserEntity createdUser = authUserDao.createUser(userEntity);

                    AuthorityEntity[] authorityEntities = authorities.stream()
                            .map(authorityJson -> {
                                AuthorityEntity authorityEntity = AuthorityEntity.fromJson(authorityJson);
                                if (authorityEntity.getUser() == null || authorityEntity.getUser().getId() == null) {
                                    AuthUserEntity userRef = new AuthUserEntity();
                                    userRef.setId(createdUser.getId());
                                    authorityEntity.setUser(userRef);
                                }
                                return authorityEntity;
                            })
                            .toArray(AuthorityEntity[]::new);

                    authAuthorityDao.create(authorityEntities);

                    return UserJson.fromEntity(createdUser);
                },
                isolationLevel
        );
    }
}
