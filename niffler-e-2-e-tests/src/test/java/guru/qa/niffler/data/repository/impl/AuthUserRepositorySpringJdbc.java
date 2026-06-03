package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.extractor.AuthUserWithAuthoritiesExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));

    @Override
    public AuthUserEntity createUser(AuthUserEntity user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO \"user\" (username, password, enabled, account_non_expired, " +
                            "account_non_locked, credentials_non_expired) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, passwordEncoder.encode(user.getPassword()));
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());
            return ps;
        }, keyHolder);

        UUID generatedId = (UUID) keyHolder.getKeys().get("id");
        user.setId(generatedId);

        for (AuthorityEntity authority : user.getAuthorities()) {
            jdbcTemplate.update(
                    "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
                    generatedId,
                    authority.getAuthority().name()
            );
        }

        return user;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        String sql = """
                SELECT a.id AS authority_id,
                       a.authority,
                       u.id AS user_id,
                       u.username,
                       u.password,
                       u.enabled,
                       u.account_non_expired,
                       u.account_non_locked,
                       u.credentials_non_expired
                FROM "user" u
                LEFT JOIN authority a ON u.id = a.user_id
                WHERE u.id = ?
                """;

        return jdbcTemplate.query(sql, AuthUserWithAuthoritiesExtractor.instance, id);
    }
}