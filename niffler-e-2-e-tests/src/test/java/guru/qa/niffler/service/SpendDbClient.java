package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.SpendJson;
import java.sql.SQLException;
import java.util.Optional;

public class SpendDbClient {

    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    public SpendJson createSpend(SpendJson spend) throws SQLException {
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

        return SpendJson.fromEntity(spendDao.create(spendEntity));
    }
}
