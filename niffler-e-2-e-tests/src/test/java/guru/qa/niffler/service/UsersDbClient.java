package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserDataEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserdataUserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserDao authUserDao =
            new AuthUserDaoJdbc(DataSources.xaDataSource(CFG.authJdbcUrl()));
    private final AuthAuthorityDao authAuthorityDao =
            new AuthAuthorityDaoJdbc(DataSources.xaDataSource(CFG.authJdbcUrl()));
    private final UserdataUserDao userdataUserDao =
            new UserdataUserDaoSpringJdbc(DataSources.xaDataSource(CFG.userdataJdbcUrl()));

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    public UserdataUserJson createUserSpringJdbc(UserdataUserJson user) {
        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(user.username());
                    authUser.setPassword(pe.encode("12345"));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);

                    AuthUserEntity createdAuthUser = authUserDao.createUser(authUser);

                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                                    e -> {
                                        AuthorityEntity ae = new AuthorityEntity();
                                        AuthUserEntity userEntity = new AuthUserEntity();
                                        userEntity.setId(createdAuthUser.getId());
                                        ae.setUser(userEntity);
                                        ae.setAuthority(e);
                                        return ae;
                                    })
                            .toArray(AuthorityEntity[]::new);

                    authAuthorityDao.create(authorityEntities);
                    return UserdataUserJson.fromEntity(
                            userdataUserDao.createUser(UserDataEntity.fromJson(user))
                    );
                }
        );
    }
}