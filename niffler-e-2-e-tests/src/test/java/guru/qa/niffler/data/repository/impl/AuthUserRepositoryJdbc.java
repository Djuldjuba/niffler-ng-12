package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        try (PreparedStatement userPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked," +
                        "credentials_non_expired) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement authorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                     "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)")) {
            userPs.setString(1, user.getUsername());
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            userPs.setString(2, encodedPassword);
            userPs.setBoolean(3, user.getEnabled());
            userPs.setBoolean(4, user.getAccountNonExpired());
            userPs.setBoolean(5, user.getAccountNonLocked());
            userPs.setBoolean(6, user.getCredentialsNonExpired());

            userPs.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = userPs.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can't find id in ResultSet");
                }
            }
            user.setId(generatedKey);
            for (AuthorityEntity a : user.getAuthorities()) {
                authorityPs.setObject(1, generatedKey);
                authorityPs.setString(2, a.getAuthority().name());
                authorityPs.addBatch();
                authorityPs.clearParameters();
            }
            authorityPs.executeBatch();

            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        try (PreparedStatement userPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "UPDATE \"user\" SET username = ?, password = ?, enabled = ?, account_non_expired = ?, " +
                        "account_non_locked = ?, credentials_non_expired = ? WHERE id = ?");
             PreparedStatement deleteAuthorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                     "DELETE FROM authority WHERE user_id = ?");
             PreparedStatement insertAuthorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                     "INSERT INTO authority (user_id, authority) VALUES (?, ?)")) {

            userPs.setString(1, user.getUsername());
            userPs.setString(2, passwordEncoder.encode(user.getPassword()));
            userPs.setBoolean(3, user.getEnabled());
            userPs.setBoolean(4, user.getAccountNonExpired());
            userPs.setBoolean(5, user.getAccountNonLocked());
            userPs.setBoolean(6, user.getCredentialsNonExpired());
            userPs.setObject(7, user.getId());

            int updatedRows = userPs.executeUpdate();
            if (updatedRows == 0) {
                throw new SQLException("User not found with id: " + user.getId());
            }

            deleteAuthorityPs.setObject(1, user.getId());
            deleteAuthorityPs.executeUpdate();

            for (AuthorityEntity a : user.getAuthorities()) {
                insertAuthorityPs.setObject(1, user.getId());
                insertAuthorityPs.setString(2, a.getAuthority().name());
                insertAuthorityPs.addBatch();
                insertAuthorityPs.clearParameters();
            }
            insertAuthorityPs.executeBatch();

            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" u JOIN repository a ON u.id = a.user_id = ?")) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                AuthUserEntity user = null;
                List<AuthorityEntity> authorityEntities = new ArrayList<>();
                while (rs.next()) {
                    if (user == null) {
                        user = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
                    }
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUser(user);
                    ae.setId(rs.getObject("a.id", UUID.class));
                    ae.setAuthority(Authority.valueOf(rs.getString("authority")));
                    authorityEntities.add(ae);

                    AuthUserEntity result = new AuthUserEntity();
                    result.setId(rs.getObject("id", UUID.class));
                    result.setUsername(rs.getString("username"));
                    result.setPassword(rs.getString("password"));
                    result.setEnabled(rs.getBoolean("enabled"));
                    result.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    result.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    result.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                }
                if (user == null) {
                    return Optional.empty();
                } else {
                    user.setAuthorities(authorityEntities);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT u.id AS user_id, u.username, u.password, u.enabled, u.account_non_expired, " +
                        "u.account_non_locked, u.credentials_non_expired, " +
                        "a.id AS authority_id, a.authority " +
                        "FROM \"user\" u " +
                        "LEFT JOIN authority a ON u.id = a.user_id " +
                        "WHERE u.username = ?")) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                AuthUserEntity user = null;
                List<AuthorityEntity> authorities = new ArrayList<>();

                while (rs.next()) {
                    if (user == null) {
                        user = new AuthUserEntity();
                        user.setId(rs.getObject("user_id", UUID.class));
                        user.setUsername(rs.getString("username"));
                        user.setPassword(rs.getString("password"));
                        user.setEnabled(rs.getBoolean("enabled"));
                        user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                        user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                        user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                        user.setAuthorities(authorities);
                    }

                    UUID authorityId = rs.getObject("authority_id", UUID.class);
                    if (authorityId != null) {
                        AuthorityEntity authority = new AuthorityEntity();
                        authority.setId(authorityId);
                        authority.setAuthority(Authority.valueOf(rs.getString("authority")));
                        authority.setUser(user);
                        authorities.add(authority);
                    }
                }

                return Optional.ofNullable(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void remove(AuthUserEntity user) {
        try (PreparedStatement deleteAuthorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "DELETE FROM authority WHERE user_id = ?");
             PreparedStatement deleteUserPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                     "DELETE FROM \"user\" WHERE id = ?")) {

            var connection = holder(CFG.authJdbcUrl()).connection();
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {
                deleteAuthorityPs.setObject(1, user.getId());
                deleteAuthorityPs.executeUpdate();

                deleteUserPs.setObject(1, user.getId());
                int deletedRows = deleteUserPs.executeUpdate();

                if (deletedRows == 0) {
                    throw new SQLException("User not found with id: " + user.getId());
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
