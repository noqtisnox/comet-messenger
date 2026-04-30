package com.comet.repository;

import com.comet.config.DatabaseManager;
import com.comet.model.Contact;
import com.comet.model.enums.ContactStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContactRepository {
    public Optional<Contact> addContactRequest(Long userId, Long contactId) {
        String sql = "INSERT INTO contacts (user_id, contact_id, status) VALUES (?, ?, ?) RETURNING *";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setLong(2, contactId);
            stmt.setString(3, ContactStatus.PENDING.name().toLowerCase()); // Store as 'pending'

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToContact(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean updateContactStatus(Long userId, Long contactId, ContactStatus newStatus) {
        String sql = "UPDATE contacts SET status = ? WHERE user_id = ? AND contact_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus.name().toLowerCase());
            stmt.setLong(2, userId);
            stmt.setLong(3, contactId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Contact> getContactsByStatus(Long userId, ContactStatus status) {
        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contacts WHERE user_id = ? AND status = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setString(2, status.name().toLowerCase());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contacts.add(mapRowToContact(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    private Contact mapRowToContact(ResultSet rs) throws SQLException {
        String statusString = rs.getString("status").toUpperCase();
        ContactStatus status = ContactStatus.valueOf(statusString);

        return new Contact(
                rs.getLong("user_id"),
                rs.getLong("contact_id"),
                status,
                rs.getTimestamp("added_at").toInstant()
        );
    }
}