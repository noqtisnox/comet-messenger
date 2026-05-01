package com.comet.repository;

import com.comet.config.DatabaseManager;
import com.comet.model.User;

import java.sql.*;
import java.util.Optional;

public class UserRepository {

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // replace with a logging framework like SLF4J
        }
        return Optional.empty();
    }

    public Optional<User> create(String username, String passwordHash) {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?) RETURNING *";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, passwordHash);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    private User mapRowToUser(ResultSet rs) throws SQLException {
        Timestamp lastActive = rs.getTimestamp("last_active_at");
        return new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                lastActive != null ? lastActive.toInstant() : null,
                rs.getTimestamp("created_at").toInstant()
        );
    }
}