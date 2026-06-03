package guru.qa.niffler.chainedTransactionManager;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserDataEntity;
import guru.qa.niffler.model.UserdataUserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

public class UsersDbClientChained {
    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
    private final UserdataUserDao userdataUserDao = new UserdataUserDaoSpringJdbc();

    private final ChainedTransactionTemplate chainedTxTemplate = new ChainedTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    public UserdataUserJson createUser(UserdataUserJson user) {
        return chainedTxTemplate.execute(() -> {
            AuthUserEntity authUser = new AuthUserEntity();
            authUser.setUsername(user.username());
            authUser.setPassword(pe.encode("12345"));
            authUser.setEnabled(true);
            authUser.setAccountNonExpired(true);
            authUser.setAccountNonLocked(true);
            authUser.setCredentialsNonExpired(true);

            AuthUserEntity createdAuthUser = authUserDao.createUser(authUser);

            AuthorityEntity[] authorities = Arrays.stream(Authority.values())
                    .map(authority -> {
                        AuthorityEntity ae = new AuthorityEntity();
                        AuthUserEntity userRef = new AuthUserEntity();
                        userRef.setId(createdAuthUser.getId());
                        ae.setUser(userRef);
                        ae.setAuthority(authority);
                        return ae;
                    })
                    .toArray(AuthorityEntity[]::new);

            authAuthorityDao.create(authorities);

            UserDataEntity userData = UserDataEntity.fromJson(user);
            userData.setUsername(user.username());

            return UserdataUserJson.fromEntity(userdataUserDao.createUser(userData));
        });
    }
}
