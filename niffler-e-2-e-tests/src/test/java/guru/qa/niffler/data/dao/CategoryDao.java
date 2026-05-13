package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryDao {

    CategoryEntity create(CategoryEntity category) throws SQLException;

    Optional<CategoryEntity> findCategoryById(UUID id) throws SQLException;

    Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) throws SQLException;

    List<CategoryEntity> findAllByUsername(String username) throws SQLException;

    void deleteCategory(CategoryEntity category) throws SQLException;
}
