package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();

    private final DataSource ds;

    public AuthAuthorityDaoJdbc() {
        this(DataSources.dataSource(CFG.authJdbcUrl()));
    }

    public AuthAuthorityDaoJdbc(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public void create(AuthorityEntity... authority) {
        Connection connection = DataSourceUtils.getConnection(ds);
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO authority (user_id, authority) VALUES (?, ?)"
        )) {
            for (AuthorityEntity a : authority) {
                ps.setObject(1, a.getUser().getId());
                ps.setString(2, a.getAuthority().name());
                ps.addBatch();
                ps.clearParameters();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, ds);
        }
    }
}