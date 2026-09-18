package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userdata.UserDataEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserdataUserDao {

    UserDataEntity createUser(UserDataEntity user);

    Optional<UserDataEntity> findById(UUID id);

    Optional<UserDataEntity> findByUsername(String username);

    void delete(UserDataEntity user);
}
