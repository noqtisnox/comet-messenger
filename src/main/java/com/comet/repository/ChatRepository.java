package com.comet.repository;

import com.comet.config.DatabaseManager;
import com.comet.model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatRepository {

    public Message saveMessage(Long chatId, Long senderId, String content) {
        String sql = "INSERT INTO messages (chat_id, sender_id, content) VALUES (?, ?, ?) RETURNING *";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, chatId);
            stmt.setLong(2, senderId);
            stmt.setString(3, content);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMessage(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if it fails, which the Service layer will handle
    }

    public List<Message> getMessagesForChat(Long chatId, int limit) {
        List<Message> messages = new ArrayList<>();

        // Grab the newest 'limit' messages, but return them sorted oldest to newest
        String sql = "SELECT * FROM (" +
                "  SELECT * FROM messages WHERE chat_id = ? ORDER BY created_at DESC LIMIT ?" +
                ") sub ORDER BY created_at ASC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, chatId);
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapRowToMessage(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    private Message mapRowToMessage(ResultSet rs) throws SQLException {
        Timestamp updated = rs.getTimestamp("updated_at");
        return new Message(
                rs.getLong("id"),
                rs.getLong("chat_id"),
                rs.getLong("sender_id"), // Will be 0 or null if sender was deleted
                rs.getString("content"),
                rs.getBoolean("is_deleted"),
                updated != null ? updated.toInstant() : null,
                rs.getTimestamp("created_at").toInstant()
        );
    }
}