package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AuthUserWithAuthoritiesExtractor implements ResultSetExtractor<Optional<AuthUserEntity>> {

    public static final AuthUserWithAuthoritiesExtractor instance = new AuthUserWithAuthoritiesExtractor();

    private AuthUserWithAuthoritiesExtractor() {
    }

    @Override
    public Optional<AuthUserEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
        AuthUserEntity user = null;
        Map<UUID, AuthorityEntity> authoritiesMap = new HashMap<>();

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
                user.setAuthorities(new ArrayList<>());
            }

            UUID authorityId = rs.getObject("authority_id", UUID.class);
            if (authorityId != null && !authoritiesMap.containsKey(authorityId)) {
                AuthorityEntity authority = new AuthorityEntity();
                authority.setId(authorityId);
                authority.setAuthority(Authority.valueOf(rs.getString("authority")));
                authority.setUser(user);
                authoritiesMap.put(authorityId, authority);
                user.getAuthorities().add(authority);
            }
        }

        return Optional.ofNullable(user);
    }
}
