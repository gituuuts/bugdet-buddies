package com.budgetbuddies.dao;

import com.budgetbuddies.config.DBConnection;
import com.budgetbuddies.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    // CREATE
    public boolean addCategory(Category category) {
        String sql = "INSERT INTO Categories " +
            "(C_id, C_name, C_type, Users_U_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.getC_id());
            ps.setString(2, category.getC_name());
            ps.setString(3, category.getC_type());
            ps.setString(4, category.getUsers_U_id());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ
    public List<Category> getAllCategories(String userId) {
        List<Category> categories = new ArrayList<>();

        String sql = "SELECT * FROM Categories WHERE Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Category category = new Category(
                    rs.getString("C_id"),
                    rs.getString("C_name"),
                    rs.getString("C_type"),
                    rs.getString("Users_U_id")
                );

                categories.add(category);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

// READ: Menampilkan satu kategori berdasarkan ID dan user
public Category findById(String id, String userId) {
    String sql = "SELECT * FROM Categories WHERE C_id = ? AND Users_U_id = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, id);
        ps.setString(2, userId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Category(
                    rs.getString("C_id"),
                    rs.getString("C_name"),
                    rs.getString("C_type"),
                    rs.getString("Users_U_id")
                );
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return null;
}

    // UPDATE
    public boolean updateCategory(Category category) {
        String sql = "UPDATE Categories SET C_name = ?, C_type = ? " +
            "WHERE C_id = ? AND Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.getC_name());
            ps.setString(2, category.getC_type());
            ps.setString(3, category.getC_id());
            ps.setString(4, category.getUsers_U_id());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean deleteCategory(String categoryId, String userId) {
        String sql = "DELETE FROM Categories " +
            "WHERE C_id = ? AND Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, categoryId);
            ps.setString(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
