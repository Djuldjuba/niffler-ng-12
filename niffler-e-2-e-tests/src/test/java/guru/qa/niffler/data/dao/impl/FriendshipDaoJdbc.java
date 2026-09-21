package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.FriendshipDao;
import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class FriendshipDaoJdbc implements FriendshipDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public void addIncomeInvitation(UserEntity requester, UserEntity addressee) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO friendship (requester_id, addressee_id, status, created_date) VALUES (?, ?, ?, ?)"
        )) {
            ps.setObject(1, requester.getId());
            ps.setObject(2, addressee.getId());
            ps.setString(3, "PENDING");
            ps.setDate(4, new java.sql.Date(new Date().getTime()));

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add income invitation from " + requester.getUsername() + " to " + addressee.getUsername(), e);
        }
    }

    @Override
    public void addOutcomeInvitation(UserEntity requester, UserEntity addressee) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO friendship (requester_id, addressee_id, status, created_date) VALUES (?, ?, ?, ?)"
        )) {
            ps.setObject(1, requester.getId());
            ps.setObject(2, addressee.getId());
            ps.setString(3, "PENDING");
            ps.setDate(4, new java.sql.Date(new Date().getTime()));

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add outcome invitation from " + requester.getUsername() + " to " + addressee.getUsername(), e);
        }
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        try {
            try (PreparedStatement checkPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                    "SELECT status FROM friendship WHERE requester_id = ? AND addressee_id = ?"
            )) {
                checkPs.setObject(1, requester.getId());
                checkPs.setObject(2, addressee.getId());

                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        String currentStatus = rs.getString("status");
                        if ("PENDING".equals(currentStatus)) {
                            try (PreparedStatement updatePs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                                    "UPDATE friendship SET status = ? WHERE requester_id = ? AND addressee_id = ?"
                            )) {
                                updatePs.setString(1, "ACCEPTED");
                                updatePs.setObject(2, requester.getId());
                                updatePs.setObject(3, addressee.getId());
                                updatePs.executeUpdate();
                            }
                        }
                    } else {
                        try (PreparedStatement insertPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                                "INSERT INTO friendship (requester_id, addressee_id, status, created_date) VALUES (?, ?, ?, ?)"
                        )) {
                            insertPs.setObject(1, requester.getId());
                            insertPs.setObject(2, addressee.getId());
                            insertPs.setString(3, "ACCEPTED");
                            insertPs.setDate(4, new java.sql.Date(new Date().getTime()));
                            insertPs.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add friend between " + requester.getUsername() + " and " + addressee.getUsername(), e);
        }
    }

    @Override
    public void removeFriendships(UUID userId) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "DELETE FROM friendship WHERE requester_id = ? OR addressee_id = ?"
        )) {
            ps.setObject(1, userId);
            ps.setObject(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete friendships for user: " + userId, e);
        }
    }
}
