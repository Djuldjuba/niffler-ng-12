package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.FriendshipDao;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;

public class FriendshipDaoSpringJdbc implements FriendshipDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public void addIncomeInvitation(UserEntity requester, UserEntity addressee) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(
                "INSERT INTO friendship (requester_id, addressee_id, status, created_date) VALUES (?, ?, ?, ?)",
                requester.getId(),
                addressee.getId(),
                "PENDING",
                new java.sql.Date(new Date().getTime())
        );
    }

    @Override
    public void addOutcomeInvitation(UserEntity requester, UserEntity addressee) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(
                "INSERT INTO friendship (requester_id, addressee_id, status, created_date) VALUES (?, ?, ?, ?)",
                requester.getId(),
                addressee.getId(),
                "PENDING",
                new java.sql.Date(new Date().getTime())
        );
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM friendship WHERE requester_id = ? AND addressee_id = ?",
                Integer.class,
                requester.getId(),
                addressee.getId()
        );

        if (count != null && count > 0) {
            jdbcTemplate.update(
                    "UPDATE friendship SET status = ? WHERE requester_id = ? AND addressee_id = ?",
                    "ACCEPTED",
                    requester.getId(),
                    addressee.getId()
            );
        } else {
            jdbcTemplate.update(
                    "INSERT INTO friendship (requester_id, addressee_id, status, created_date) VALUES (?, ?, ?, ?)",
                    requester.getId(),
                    addressee.getId(),
                    "ACCEPTED",
                    new java.sql.Date(new Date().getTime())
            );
        }
    }
}
