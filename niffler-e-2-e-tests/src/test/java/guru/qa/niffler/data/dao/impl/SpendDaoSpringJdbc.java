package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
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

public class SpendDaoSpringJdbc implements SpendDao {

    private static final Config CFG = Config.getInstance();

    private JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
    }

    @Override
    public SpendEntity create(SpendEntity spend) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate().update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) "
                            + "VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, spend.getUsername());
            ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());
            return ps;
        }, keyHolder);

        final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        spend.setId(generatedKey);
        return spend;
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        int updatedRows = jdbcTemplate().update(
                "UPDATE spend SET username = ?, spend_date = ?, currency = ?, amount = ?, description = ?, category_id = ? WHERE id = ?",
                spend.getUsername(),
                new java.sql.Date(spend.getSpendDate().getTime()),
                spend.getCurrency().name(),
                spend.getAmount(),
                spend.getDescription(),
                spend.getCategory().getId(),
                spend.getId()
        );

        if (updatedRows == 0) {
            throw new RuntimeException("Spend not found with id: " + spend.getId());
        }
        return spend;
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID uuid) {
        try {
            SpendEntity spend = jdbcTemplate().queryForObject(
                    "SELECT * FROM spend WHERE id = ?",
                    SpendEntityRowMapper.instance,
                    uuid
            );
            return Optional.ofNullable(spend);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<SpendEntity> findByUsernameAndDescription(String username, String description) {
        try {
            SpendEntity spend = jdbcTemplate().queryForObject(
                    "SELECT * FROM spend WHERE username = ? AND description = ?",
                    SpendEntityRowMapper.instance,
                    username, description
            );
            return Optional.ofNullable(spend);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        try {
            return jdbcTemplate().query(
                    "SELECT * FROM spend WHERE username = ?",
                    SpendEntityRowMapper.instance,
                    username
            );
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    @Override
    public void deleteSpend(SpendEntity spend) {
        jdbcTemplate().update(
                "DELETE FROM spend WHERE id = ?",
                spend.getId()
        );
    }

    @Override
    public List<SpendEntity> findAll() {
        try {
            return jdbcTemplate().query(
                    "SELECT * FROM spend",
                    SpendEntityRowMapper.instance
            );
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }
}