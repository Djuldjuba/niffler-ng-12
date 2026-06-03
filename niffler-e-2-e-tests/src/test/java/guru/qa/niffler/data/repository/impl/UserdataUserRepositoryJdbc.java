package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.FriendshipDao;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.FriendshipDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;

import java.util.Optional;
import java.util.UUID;


public class UserdataUserRepositoryJdbc implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();
    private final UserdataUserDao udUserDao = new UserdataUserDaoSpringJdbc();
    private final FriendshipDao friendshipDao = new FriendshipDaoJdbc(); // Используем FriendshipDao

    @Override
    public UserEntity create(UserEntity user) {
        return udUserDao.createUser(user);
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        return udUserDao.findById(id);
    }

    @Override
    public void addIncomeInvitation(UserEntity requester, UserEntity addressee) {
        friendshipDao.addIncomeInvitation(requester, addressee);
    }

    @Override
    public void addOutcomeInvitation(UserEntity requester, UserEntity addressee) {
        friendshipDao.addOutcomeInvitation(requester, addressee);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        friendshipDao.addFriend(requester, addressee);
    }
}
