package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoSpringJdbc implements CategoryDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public CategoryEntity create(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO category (name, username, archived) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, category.getName());
            ps.setString(2, category.getUsername());
            ps.setBoolean(3, category.isArchived());
            return ps;
        }, keyHolder);
        final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        category.setId(generatedKey);
        return category;
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        try {
            CategoryEntity category = jdbcTemplate.queryForObject(
                    "SELECT * FROM category WHERE id = ?",
                    CategoryEntityRowMapper.instance,
                    id
            );
            return Optional.ofNullable(category);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        try {
            CategoryEntity category = jdbcTemplate.queryForObject(
                    "SELECT * FROM category WHERE username = ? AND name = ?",
                    CategoryEntityRowMapper.instance,
                    username, categoryName
            );
            return Optional.ofNullable(category);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<CategoryEntity> findAllByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM category WHERE username = ?",
                    CategoryEntityRowMapper.instance,
                    username
            );
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    @Override
    public void deleteCategory(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        jdbcTemplate.update(
                "DELETE FROM category WHERE id = ?",
                category.getId()
        );
    }

    @Override
    public List<CategoryEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM category",
                    CategoryEntityRowMapper.instance
            );
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }
}
