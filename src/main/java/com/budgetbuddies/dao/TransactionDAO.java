package com.budgetbuddies.dao;

import com.budgetbuddies.config.DBConnection;
import com.budgetbuddies.model.Transaction;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    // CREATE
    public boolean addTransaction(Transaction transaction, String userId) {
        String sql = "INSERT INTO Transactions " +
            "(T_id, T_title, T_description, T_type, T_amount, " +
            "T_transaction_date, Accounts_A_id, Categories_C_id) " +
            "SELECT ?, ?, ?, ?, ?, ?, A.A_id, C.C_id " +
            "FROM Accounts A, Categories C " +
            "WHERE A.A_id = ? AND A.Users_U_id = ? " +
            "AND C.C_id = ? AND C.Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, transaction.getT_id());
            ps.setString(2, transaction.getT_title());
            ps.setString(3, transaction.getT_description());
            ps.setString(4, transaction.getT_type());
            ps.setBigDecimal(5, transaction.getT_amount());
            ps.setDate(6, transaction.getT_transaction_date());
            ps.setString(7, transaction.getAccounts_A_id());
            ps.setString(8, userId);
            ps.setString(9, transaction.getCategories_C_id());
            ps.setString(10, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ ALL
    public List<Transaction> getAllTransactions(String userId) {
        List<Transaction> transactions = new ArrayList<>();

        String sql = "SELECT T.* FROM Transactions T " +
            "JOIN Accounts A ON T.Accounts_A_id = A.A_id " +
            "WHERE A.Users_U_id = ? " +
            "ORDER BY T.T_transaction_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapTransaction(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    // READ BY ID
    public Transaction findById(String id, String userId) {
        String sql = "SELECT T.* FROM Transactions T " +
            "JOIN Accounts A ON T.Accounts_A_id = A.A_id " +
            "WHERE T.T_id = ? AND A.Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setString(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTransaction(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // SEARCH BY TITLE
    public List<Transaction> searchByTitle(String userId, String keyword) {
        List<Transaction> transactions = new ArrayList<>();

        String sql = "SELECT T.* FROM Transactions T " +
            "JOIN Accounts A ON T.Accounts_A_id = A.A_id " +
            "WHERE A.Users_U_id = ? " +
            "AND T.T_title LIKE ? " +
            "ORDER BY T.T_transaction_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setString(2, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapTransaction(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    // FILTER BY DATE RANGE AND TYPE
    public List<Transaction> findByDateRange(
            String userId, Date from, Date to, String type) {

        List<Transaction> transactions = new ArrayList<>();

        String sql = "SELECT T.* FROM Transactions T " +
            "JOIN Accounts A ON T.Accounts_A_id = A.A_id " +
            "WHERE A.Users_U_id = ? " +
            "AND T.T_transaction_date BETWEEN ? AND ? " +
            "AND (? IS NULL OR T.T_type = ?) " +
            "ORDER BY T.T_transaction_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setDate(2, from);
            ps.setDate(3, to);
            ps.setString(4, type);
            ps.setString(5, type);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapTransaction(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

// Menghitung total pemasukan user
public BigDecimal getTotalIncome(String userId) {
    return getTotalByType(userId, "Income");
}

// Menghitung total pengeluaran user
public BigDecimal getTotalExpense(String userId) {
    return getTotalByType(userId, "Expense");
}

// Helper untuk menghitung transaksi berdasarkan tipe
private BigDecimal getTotalByType(String userId, String type) {
    String sql = "SELECT COALESCE(SUM(T.T_amount), 0) AS total " +
        "FROM Transactions T " +
        "JOIN Accounts A ON T.Accounts_A_id = A.A_id " +
        "WHERE A.Users_U_id = ? AND T.T_type = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, userId);
        ps.setString(2, type);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal("total");
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return BigDecimal.ZERO;
}
    // UPDATE
    public boolean updateTransaction(Transaction transaction, String userId) {
        String sql = "UPDATE Transactions T " +
            "JOIN Accounts A ON T.Accounts_A_id = A.A_id " +
            "JOIN Accounts A2 ON A2.A_id = ? AND A2.Users_U_id = ? " +
            "JOIN Categories C ON C.C_id = ? AND C.Users_U_id = ? " +
            "SET T.T_title = ?, T.T_description = ?, T.T_type = ?, " +
            "T.T_amount = ?, T.T_transaction_date = ?, " +
            "T.Accounts_A_id = A2.A_id, T.Categories_C_id = C.C_id " +
            "WHERE T.T_id = ? AND A.Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, transaction.getAccounts_A_id());
            ps.setString(2, userId);
            ps.setString(3, transaction.getCategories_C_id());
            ps.setString(4, userId);
            ps.setString(5, transaction.getT_title());
            ps.setString(6, transaction.getT_description());
            ps.setString(7, transaction.getT_type());
            ps.setBigDecimal(8, transaction.getT_amount());
            ps.setDate(9, transaction.getT_transaction_date());
            ps.setString(10, transaction.getT_id());
            ps.setString(11, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean deleteTransaction(String transactionId, String userId) {
        String sql = "DELETE T FROM Transactions T " +
            "JOIN Accounts A ON T.Accounts_A_id = A.A_id " +
            "WHERE T.T_id = ? AND A.Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, transactionId);
            ps.setString(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // MAP RESULTSET TO MODEL
    private Transaction mapTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
            rs.getString("T_id"),
            rs.getString("T_title"),
            rs.getString("T_description"),
            rs.getString("T_type"),
            rs.getBigDecimal("T_amount"),
            rs.getDate("T_transaction_date"),
            rs.getString("Accounts_A_id"),
            rs.getString("Categories_C_id")
        );
    }
}
