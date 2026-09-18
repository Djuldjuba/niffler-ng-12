package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    @Override
    public SpendJson createSpending(SpendJson spending) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(
                new SingleConnectionDataSource(
                        CFG.spendJdbcUrl(),
                        CFG.dbUsername(),
                        CFG.dbPassword(),
                        false
                )
        );

        final CategoryJson category = findByUsernameAndName(
                spending.username(),
                spending.category().name()
        ).orElseGet(() -> createCategory(spending.category()));

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(
                            """
                                      INSERT INTO "spend" (username, spend_date, currency, amount, description, category_id) VALUES (?, ?, ?, ?, ?, ?)
                                    """,
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setString(1, spending.username());
                    ps.setDate(2, new java.sql.Date(spending.spendDate().getTime()));
                    ps.setString(3, spending.currency().name());
                    ps.setDouble(4, spending.amount());
                    ps.setString(5, spending.description());
                    ps.setObject(6, category.id());
                    return ps;
                },
                keyHolder
        );

        return new SpendJson(
                (UUID) keyHolder.getKeys().get("id"),
                spending.spendDate(),
                category,
                spending.currency(),
                spending.amount(),
                spending.description(),
                spending.username()
        );
    }

    @Override
    public CategoryJson createCategory(CategoryJson category) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(
                new SingleConnectionDataSource(
                        CFG.spendJdbcUrl(),
                        CFG.dbUsername(),
                        CFG.dbPassword(),
                        false
                )
        );

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(
                            """
                                    INSERT INTO category (name, username, archived)
                                    VALUES (?, ?, ?)
                                    """,
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setString(1, category.name());
                    ps.setString(2, category.username());
                    ps.setBoolean(3, category.archived());
                    return ps;
                },
                keyHolder
        );

        return new CategoryJson(
                (UUID) keyHolder.getKeys().get("id"),
                category.name(),
                category.username(),
                category.archived()
        );
    }

    @Override
    public Optional<CategoryJson> findByUsernameAndName(String username, String name) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(
                new SingleConnectionDataSource(
                        CFG.spendJdbcUrl(),
                        CFG.dbUsername(),
                        CFG.dbPassword(),
                        false
                )
        );
        return jdbcTemplate.query(
                """
                        SELECT * FROM category WHERE username = ? AND name = ?
                        """,
                (rs, num) -> new CategoryJson(
                        (UUID) rs.getObject("id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getBoolean("archived")
                ),
                username,
                name
        ).stream().findFirst();
    }

    @Override
    public void removeCategory(CategoryJson category) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(
                new SingleConnectionDataSource(
                        CFG.spendJdbcUrl(),
                        CFG.dbUsername(),
                        CFG.dbPassword(),
                        false
                )
        );

        jdbcTemplate.update(
                "DELETE FROM category WHERE id = ?",
                category.id()
        );
    }

    @Override
    public void removeSpending(SpendJson spending) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(
                new SingleConnectionDataSource(
                        CFG.spendJdbcUrl(),
                        CFG.dbUsername(),
                        CFG.dbPassword(),
                        false
                )
        );
        jdbcTemplate.update("DELETE FROM spend WHERE id = ?", spending.id());
    }
}
