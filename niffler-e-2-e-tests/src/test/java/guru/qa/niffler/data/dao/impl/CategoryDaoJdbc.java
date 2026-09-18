package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoJdbc implements CategoryDao {

    private static final Config CFG = Config.getInstance();
    private final DataSource ds = DataSources.dataSource(CFG.spendJdbcUrl());

    private CategoryEntity mapResultSetToCategory(ResultSet rs) throws SQLException {
        CategoryEntity ce = new CategoryEntity();
        ce.setId(rs.getObject("id", UUID.class));
        ce.setUsername(rs.getString("username"));
        ce.setName(rs.getString("name"));
        ce.setArchived(rs.getBoolean("archived"));
        return ce;
    }

    @Override
    public CategoryEntity create(CategoryEntity category) {
        Connection connection = DataSourceUtils.getConnection(ds);
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO category (username, name, archived) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, category.getUsername());
            ps.setString(2, category.getName());
            ps.setBoolean(3, category.isArchived());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can't find id in ResultSet");
                }
            }
            category.setId(generatedKey);
            return category;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, ds);
        }
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        Connection connection = DataSourceUtils.getConnection(ds);
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM category WHERE id = ?"
        )) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCategory(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, ds);
        }
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        Connection connection = DataSourceUtils.getConnection(ds);
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM category WHERE username = ? AND name = ?"
        )) {
            ps.setString(1, username);
            ps.setString(2, categoryName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCategory(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, ds);
        }
    }

    @Override
    public List<CategoryEntity> findAllByUsername(String username) {
        Connection connection = DataSourceUtils.getConnection(ds);
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM category WHERE username = ?"
        )) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                List<CategoryEntity> categories = new ArrayList<>();
                while (rs.next()) {
                    categories.add(mapResultSetToCategory(rs));
                }
                return categories;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, ds);
        }
    }

    @Override
    public void deleteCategory(CategoryEntity category) {
        Connection connection = DataSourceUtils.getConnection(ds);
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM category WHERE id = ?"
        )) {
            ps.setObject(1, category.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, ds);
        }
    }

    @Override
    public List<CategoryEntity> findAll() {
        Connection connection = DataSourceUtils.getConnection(ds);
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM category")) {
            try (ResultSet rs = ps.executeQuery()) {
                List<CategoryEntity> categories = new ArrayList<>();
                while (rs.next()) {
                    categories.add(mapResultSetToCategory(rs));
                }
                return categories;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, ds);
        }
    }
}