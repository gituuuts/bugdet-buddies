package com.budgetbuddies.dao;

import com.budgetbuddies.config.DBConnection;
import com.budgetbuddies.model.Budget;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {

    // CREATE
    public boolean addBudget(Budget budget) {
        String sql = "INSERT INTO Budgets " +
            "(B_id, B_amount_limit, B_month, B_year, " +
            "Categories_C_id, Users_U_id) " +
            "SELECT ?, ?, ?, ?, C.C_id, ? " +
            "FROM Categories C " +
            "WHERE C.C_id = ? AND C.Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, budget.getB_id());
            ps.setBigDecimal(2, budget.getB_amount_limit());
            ps.setInt(3, budget.getB_month());
            ps.setInt(4, budget.getB_year());
            ps.setString(5, budget.getUsers_U_id());
            ps.setString(6, budget.getCategories_C_id());
            ps.setString(7, budget.getUsers_U_id());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ
    public List<Budget> getAllBudgets(String userId) {
        List<Budget> budgets = new ArrayList<>();

        String sql = "SELECT * FROM Budgets " +
            "WHERE Users_U_id = ? " +
            "ORDER BY B_year DESC, B_month DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Budget budget = new Budget(
                    rs.getString("B_id"),
                    rs.getBigDecimal("B_amount_limit"),
                    rs.getInt("B_month"),
                    rs.getInt("B_year"),
                    rs.getString("Categories_C_id"),
                    rs.getString("Users_U_id")
                );

                budgets.add(budget);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return budgets;
    }

// READ: Menampilkan satu budget berdasarkan ID dan user
public Budget findById(String id, String userId) {
    String sql = "SELECT * FROM Budgets " +
                 "WHERE B_id = ? AND Users_U_id = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, id);
        ps.setString(2, userId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Budget(
                    rs.getString("B_id"),
                    rs.getBigDecimal("B_amount_limit"),
                    rs.getInt("B_month"),
                    rs.getInt("B_year"),
                    rs.getString("Categories_C_id"),
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
    public boolean updateBudget(Budget budget) {
        String sql = "UPDATE Budgets B " +
            "JOIN Categories C ON C.C_id = ? " +
            "AND C.Users_U_id = B.Users_U_id " +
            "SET B.B_amount_limit = ?, " +
            "B.B_month = ?, B.B_year = ?, " +
            "B.Categories_C_id = C.C_id " +
            "WHERE B.B_id = ? AND B.Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, budget.getCategories_C_id());
            ps.setBigDecimal(2, budget.getB_amount_limit());
            ps.setInt(3, budget.getB_month());
            ps.setInt(4, budget.getB_year());
            ps.setString(5, budget.getB_id());
            ps.setString(6, budget.getUsers_U_id());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

// Menghitung total pengeluaran per kategori, bulan, dan tahun
public BigDecimal getUsedAmount(
        String userId, String categoryId, int month, int year) {

    String sql = "SELECT COALESCE(SUM(T.T_amount), 0) AS total_expense " +
        "FROM Transactions T " +
        "JOIN Accounts A ON T.Accounts_A_id = A.A_id " +
        "JOIN Categories C ON T.Categories_C_id = C.C_id " +
        "WHERE A.Users_U_id = ? " +
        "AND C.Users_U_id = ? " +
        "AND T.Categories_C_id = ? " +
        "AND T.T_type = 'Expense' " +
        "AND MONTH(T.T_transaction_date) = ? " +
        "AND YEAR(T.T_transaction_date) = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, userId);
        ps.setString(2, userId);
        ps.setString(3, categoryId);
        ps.setInt(4, month);
        ps.setInt(5, year);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal("total_expense");
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return BigDecimal.ZERO;
}

// Menghitung sisa budget per kategori, bulan, dan tahun
public BigDecimal getRemainingAmount(
        String userId, String categoryId, int month, int year) {

    String sql = "SELECT B_amount_limit FROM Budgets " +
        "WHERE Users_U_id = ? " +
        "AND Categories_C_id = ? " +
        "AND B_month = ? AND B_year = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, userId);
        ps.setString(2, categoryId);
        ps.setInt(3, month);
        ps.setInt(4, year);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                BigDecimal budgetLimit =
                    rs.getBigDecimal("B_amount_limit");

                BigDecimal usedAmount = getUsedAmount(
                    userId, categoryId, month, year
                );

                return budgetLimit.subtract(usedAmount);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return null;
}

    // DELETE
    public boolean deleteBudget(String budgetId, String userId) {
        String sql = "DELETE FROM Budgets " +
            "WHERE B_id = ? AND Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, budgetId);
            ps.setString(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
