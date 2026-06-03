package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.userdata.UserDataEntity;

import java.util.Optional;
import java.util.UUID;

public class UserdataUserDaoSpringJdbc implements UserdataUserDao {

    @Override
    public UserDataEntity createUser(UserDataEntity user) {
        return null;
    }

    @Override
    public Optional<UserDataEntity> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<UserDataEntity> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public void delete(UserDataEntity user) {
    }
}
