package com.budgetbuddies.dao;

import com.budgetbuddies.config.DBConnection;
import com.budgetbuddies.model.Account;

import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    // READ: Menampilkan semua akun milik user
    public List<Account> getAllAccounts(String userId) {
        List<Account> accounts = new ArrayList<>();

        String sql = "SELECT * FROM Accounts WHERE Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Account account = new Account(
                    rs.getString("A_id"),
                    rs.getString("A_name"),
                    rs.getString("A_account_type"),
                    rs.getBigDecimal("A_initial_balance"),
                    rs.getString("Users_U_id")
                );

                accounts.add(account);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accounts;
    }

    // CREATE: Menambahkan akun
    public boolean addAccount(Account account) {
        String sql = "INSERT INTO Accounts " +
            "(A_id, A_name, A_account_type, A_initial_balance, Users_U_id) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, account.getA_id());
            ps.setString(2, account.getA_name());
            ps.setString(3, account.getA_account_type());
            ps.setBigDecimal(4, account.getA_initial_balance());
            ps.setString(5, account.getUsers_U_id());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
// READ: Menampilkan satu akun berdasarkan ID dan user
public Account findById(String id, String userId) {
    String sql = "SELECT * FROM Accounts WHERE A_id = ? AND Users_U_id = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, id);
        ps.setString(2, userId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Account(
                    rs.getString("A_id"),
                    rs.getString("A_name"),
                    rs.getString("A_account_type"),
                    rs.getBigDecimal("A_initial_balance"),
                    rs.getString("Users_U_id")
                );
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return null;
}
    // UPDATE: Mengubah akun
    public boolean updateAccount(Account account) {
        String sql = "UPDATE Accounts SET " +
            "A_name = ?, A_account_type = ?, A_initial_balance = ? " +
            "WHERE A_id = ? AND Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, account.getA_name());
            ps.setString(2, account.getA_account_type());
            ps.setBigDecimal(3, account.getA_initial_balance());
            ps.setString(4, account.getA_id());
            ps.setString(5, account.getUsers_U_id());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

// Menghitung total saldo seluruh akun user
public BigDecimal getTotalBalance(String userId) {
    String sql = "SELECT COALESCE(SUM(" +
        "A.A_initial_balance + " +
        "COALESCE(T.total_income, 0) - " +
        "COALESCE(T.total_expense, 0)), 0) AS total_balance " +
        "FROM Accounts A " +
        "LEFT JOIN (" +
        "SELECT Accounts_A_id, " +
        "SUM(CASE WHEN T_type = 'Income' THEN T_amount ELSE 0 END) AS total_income, " +
        "SUM(CASE WHEN T_type = 'Expense' THEN T_amount ELSE 0 END) AS total_expense " +
        "FROM Transactions GROUP BY Accounts_A_id" +
        ") T ON A.A_id = T.Accounts_A_id " +
        "WHERE A.Users_U_id = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, userId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal("total_balance");
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return BigDecimal.ZERO;
}

// Menghitung saldo terkini masing-masing akun
public Map<String, BigDecimal> getAccountBalances(String userId) {
    Map<String, BigDecimal> balances = new HashMap<>();

    String sql = "SELECT A.A_id, " +
        "A.A_initial_balance + " +
        "COALESCE(SUM(CASE " +
        "WHEN T.T_type = 'Income' THEN T.T_amount " +
        "WHEN T.T_type = 'Expense' THEN -T.T_amount " +
        "ELSE 0 END), 0) AS current_balance " +
        "FROM Accounts A " +
        "LEFT JOIN Transactions T ON A.A_id = T.Accounts_A_id " +
        "WHERE A.Users_U_id = ? " +
        "GROUP BY A.A_id, A.A_initial_balance";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, userId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                balances.put(
                    rs.getString("A_id"),
                    rs.getBigDecimal("current_balance")
                );
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return balances;
}

    // DELETE: Menghapus akun
    public boolean deleteAccount(String accountId, String userId) {
        String sql = "DELETE FROM Accounts " +
            "WHERE A_id = ? AND Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountId);
            ps.setString(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
