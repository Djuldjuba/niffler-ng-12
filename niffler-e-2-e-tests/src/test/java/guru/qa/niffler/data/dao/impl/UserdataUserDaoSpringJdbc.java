package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserDaoSpringJdbc implements UserdataUserDao {

    private static final Config CFG = Config.getInstance();

    private JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
    }

    @Override
    public UserEntity createUser(UserEntity user) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate().update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());
            return ps;
        }, kh);
        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        user.setId(generatedKey);
        return user;
    }

    @Override
    public UserEntity updateUser(UserEntity user) {
        int updatedRows = jdbcTemplate().update(
                "UPDATE \"user\" SET username = ?, currency = ?, firstname = ?, surname = ?, " +
                        "photo = ?, photo_small = ?, full_name = ? WHERE id = ?",
                user.getUsername(),
                user.getCurrency().name(),
                user.getFirstname(),
                user.getSurname(),
                user.getPhoto(),
                user.getPhotoSmall(),
                user.getFullname(),
                user.getId()
        );

        if (updatedRows == 0) {
            throw new RuntimeException("User not found with id: " + user.getId());
        }
        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        try {
            UserEntity user = jdbcTemplate().queryForObject(
                    "SELECT * FROM \"user\" WHERE id = ?",
                    UserdataUserEntityRowMapper.instance,
                    id
            );
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try {
            UserEntity user = jdbcTemplate().queryForObject(
                    "SELECT * FROM \"user\" WHERE username = ?",
                    UserdataUserEntityRowMapper.instance,
                    username
            );
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(UserEntity user) {
        jdbcTemplate().update("DELETE FROM \"user\" WHERE id = ?", user.getId());
    }

    @Override
    public void deletePushTokens(UUID userId) {
        jdbcTemplate().update("DELETE FROM push_tokens WHERE user_id = ?", userId);
    }
}