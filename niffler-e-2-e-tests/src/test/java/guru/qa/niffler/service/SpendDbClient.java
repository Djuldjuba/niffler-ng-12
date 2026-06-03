package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.AuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.UserEntity;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.AuthorityJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;

import java.util.List;
import java.util.Optional;

import static guru.qa.niffler.data.Databases.transaction;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();

    public SpendJson createSpend(SpendJson spend, int isolationLevel) {
        return transaction(connection -> {
            CategoryDaoJdbc categoryDao = new CategoryDaoJdbc(connection);
            Optional<CategoryEntity> existingCategory = categoryDao
                    .findCategoryByUsernameAndCategoryName(
                            spend.username(),
                            spend.category().name()
                    );

            CategoryEntity categoryEntity;
            if (existingCategory.isPresent()) {
                categoryEntity = existingCategory.get();
            } else {
                CategoryEntity newCategory = CategoryEntity.fromJson(spend.category());
                categoryEntity = categoryDao.create(newCategory);
            }
            
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            spendEntity.setCategory(categoryEntity);

            SpendEntity createdSpend = new SpendDaoJdbc(connection).create(spendEntity);
            return SpendJson.fromEntity(createdSpend);
        }, CFG.spendJdbcUrl(), isolationLevel);
    }

    public UserJson createUserWithAuthorities(UserJson user, List<AuthorityJson> authorities, int isolationLevel) {
        return transaction(connection -> {
                    UserEntity userEntity = UserEntity.fromJson(user);
                    UserEntity createdUser = new AuthUserDaoJdbc(connection).createUser(userEntity);

                    AuthorityEntity[] authorityEntities = authorities.stream()
                            .map(authorityJson -> {
                                AuthorityEntity authorityEntity = AuthorityEntity.fromJson(authorityJson);
                                if (authorityEntity.getUser() == null || authorityEntity.getUser().getId() == null) {
                                    authorityEntity.setUser(createdUser);
                                }
                                return authorityEntity;
                            })
                            .toArray(AuthorityEntity[]::new);

                    new AuthorityDaoJdbc(connection).create(authorityEntities);
                    return UserJson.fromEntity(createdUser);
                },
                CFG.authJdbcUrl(),
                isolationLevel
        );
    }
}
