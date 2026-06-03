package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.UserEntity;

public interface AuthUserDao {
    UserEntity createUser(UserEntity user);
}
