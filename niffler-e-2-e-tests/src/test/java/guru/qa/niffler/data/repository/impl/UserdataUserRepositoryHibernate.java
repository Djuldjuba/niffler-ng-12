package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.PushTokenEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.jpa.EntityManagers;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;
import java.util.UUID;

public class UserdataUserRepositoryHibernate implements UserdataUserRepository {

    private final EntityManager entityManager = EntityManagers.em(CFG.userdataJdbcUrl());
    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity create(UserEntity user) {
        entityManager.joinTransaction();
        entityManager.persist(user);
        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(UserEntity.class, id));
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try {
            return Optional.of(entityManager.createQuery("select u from UserEntity u where u.username =:username", UserEntity.class)
                    .setParameter("username", username)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void addIncomeInvitation(UserEntity requester, UserEntity addressee) {
        entityManager.joinTransaction();
        addressee.addFriends(FriendshipStatus.PENDING, requester);
    }

    @Override
    public void addOutcomeInvitation(UserEntity requester, UserEntity addressee) {
        entityManager.joinTransaction();
        requester.addFriends(FriendshipStatus.PENDING, addressee);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        entityManager.joinTransaction();
        requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
        addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
    }

    @Override
    public UserEntity update(UserEntity user) {
        entityManager.joinTransaction();
        return entityManager.merge(user);
    }

    @Override
    public void remove(UserEntity user) {
        entityManager.joinTransaction();

        if (!entityManager.contains(user)) {
            user = entityManager.merge(user);
        }

        if (user.getFriendshipRequests() != null && !user.getFriendshipRequests().isEmpty()) {
            for (FriendshipEntity friendship : user.getFriendshipRequests()) {
                if (!entityManager.contains(friendship)) {
                    friendship = entityManager.merge(friendship);
                }
                friendship.setAddressee(null);
                entityManager.remove(friendship);
            }
            user.getFriendshipRequests().clear();
        }

        if (user.getFriendshipAddressees() != null && !user.getFriendshipAddressees().isEmpty()) {
            for (FriendshipEntity friendship : user.getFriendshipAddressees()) {
                if (!entityManager.contains(friendship)) {
                    friendship = entityManager.merge(friendship);
                }
                friendship.setRequester(null);
                entityManager.remove(friendship);
            }
            user.getFriendshipAddressees().clear();
        }

        if (user.getPushTokens() != null && !user.getPushTokens().isEmpty()) {
            for (PushTokenEntity token : user.getPushTokens()) {
                if (!entityManager.contains(token)) {
                    token = entityManager.merge(token);
                }
                token.setUser(null);
                entityManager.remove(token);
            }
            user.getPushTokens().clear();
        }

        entityManager.remove(user);
    }
}
