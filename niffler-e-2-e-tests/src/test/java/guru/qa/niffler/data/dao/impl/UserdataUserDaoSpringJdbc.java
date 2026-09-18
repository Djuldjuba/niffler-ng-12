package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.userdata.UserDataEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserDaoSpringJdbc implements UserdataUserDao {

    private static final Config CFG = Config.getInstance();

    private final DataSource ds;

    public UserdataUserDaoSpringJdbc() {
        this(DataSources.dataSource(CFG.userdataJdbcUrl()));
    }

    public UserdataUserDaoSpringJdbc(DataSource ds) {
        this.ds = ds;
    }

    private JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(ds);
    }

    @Override
    public UserDataEntity createUser(UserDataEntity user) {
        JdbcTemplate jdbcTemplate = jdbcTemplate();
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
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
    public Optional<UserDataEntity> findById(UUID id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate().queryForObject(
                            "SELECT * FROM \"user\" WHERE id = ?",
                            UserdataUserEntityRowMapper.instance,
                            id
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserDataEntity> findByUsername(String username) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate().queryForObject(
                            "SELECT * FROM \"user\" WHERE username = ?",
                            UserdataUserEntityRowMapper.instance,
                            username
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(UserDataEntity user) {
        jdbcTemplate().update("DELETE FROM \"user\" WHERE id = ?", user.getId());
    }
}