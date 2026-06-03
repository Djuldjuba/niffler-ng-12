package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.UserDataEntity;
import guru.qa.niffler.model.UserdataUserJson;

public class UserdataDbClient {

    private final UserdataUserDao userDao = new UserdataUserDaoJdbc();

    public UserdataUserJson createUser(UserdataUserJson user) {
        UserDataEntity userEntity = UserDataEntity.fromJson(user);

        return UserdataUserJson.fromEntity(
                userDao.createUser(userEntity)
        );
    }
}
