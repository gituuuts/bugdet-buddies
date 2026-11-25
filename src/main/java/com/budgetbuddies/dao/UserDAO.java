package com.budgetbuddies.dao;

import com.budgetbuddies.config.DBConnection;
import com.budgetbuddies.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Locale;

/**
 * Dipakai oleh modul authentication. Semua query memakai PreparedStatement.
 */
public class UserDAO {

    public enum InsertResult { SUCCESS, DUPLICATE_EMAIL, DUPLICATE_ID, ERROR }

    public User findByEmail(String email) {
        String sql = "SELECT U_id, U_name, U_email, U_password FROM Users WHERE U_email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User findById(String id) {
        String sql = "SELECT U_id, U_name, U_email, U_password FROM Users WHERE U_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM Users WHERE U_email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Membedakan email kembar dan ID kembar, supaya servlet tahu perlu membuat ID baru atau menolak email. */
    public InsertResult insert(User user) {
        String sql = "INSERT INTO Users (U_id, U_name, U_email, U_password) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getU_id());
            ps.setString(2, user.getU_name());
            ps.setString(3, user.getU_email());
            ps.setString(4, user.getU_password());
            return ps.executeUpdate() > 0 ? InsertResult.SUCCESS : InsertResult.ERROR;

        } catch (SQLIntegrityConstraintViolationException e) {
            String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase(Locale.ROOT);
            int i = msg.lastIndexOf("for key");
            String key = i >= 0 ? msg.substring(i) : msg;
            if (key.contains("primary")) {
                return InsertResult.DUPLICATE_ID;
            }
            if (key.contains("email")) {
                return InsertResult.DUPLICATE_EMAIL;
            }
            return InsertResult.ERROR;
        } catch (SQLException e) {
            e.printStackTrace();
            return InsertResult.ERROR;
        }
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("U_id"),
                rs.getString("U_name"),
                rs.getString("U_email"),
                rs.getString("U_password"));
    }
}
