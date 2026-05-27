package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.UserdataUserJson;

import java.sql.SQLException;

public class UserdataDbClient {

    private final UserdataUserDao userDao = new UserdataUserDaoJdbc();

    public UserdataUserJson createUser(UserdataUserJson user) throws SQLException {
        UserEntity userEntity = UserEntity.fromJson(user);

        return UserdataUserJson.fromEntity(
                userDao.createUser(userEntity)
        );
    }
}
