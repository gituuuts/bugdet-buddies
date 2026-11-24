package com.budgetbuddies.dao;

import com.budgetbuddies.config.DBConnection;
import com.budgetbuddies.model.Account;

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
