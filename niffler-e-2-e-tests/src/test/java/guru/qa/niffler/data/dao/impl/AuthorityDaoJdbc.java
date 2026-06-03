package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class AuthorityDaoJdbc implements AuthAuthorityDao {

    private final Connection connection;

    public AuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(AuthorityEntity... authorities) {
        if (authorities == null || authorities.length == 0) {
            return;
        }

        String sql = "INSERT INTO authority (user_id, authority) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (AuthorityEntity authority : authorities) {
                ps.setObject(1, authority.getUser().getId());
                ps.setObject(2, authority.getAuthority().name());
                ps.addBatch();
            }

            ps.executeBatch();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                for (AuthorityEntity authority : authorities) {
                    if (rs.next()) {
                        UUID generatedKey = rs.getObject("id", UUID.class);
                        authority.setId(generatedKey);
                    } else {
                        throw new SQLException("Can't find id in ResultSet");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
