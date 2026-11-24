package com.budgetbuddies.dao;

import com.budgetbuddies.config.DBConnection;
import com.budgetbuddies.model.RecurringTransaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecurringTransactionDAO {

    // CREATE
    public boolean addRecurringTransaction(
            RecurringTransaction transaction, String userId) {

        String sql = "INSERT INTO Recurring_Transactions " +
            "(RT_id, RT_title, RT_type, RT_amount, RT_frequency, " +
            "RT_start_date, RT_end_date, RT_next_date, RT_status, " +
            "Accounts_A_id, Categories_C_id) " +
            "SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?, A.A_id, C.C_id " +
            "FROM Accounts A, Categories C " +
            "WHERE A.A_id = ? AND A.Users_U_id = ? " +
            "AND C.C_id = ? AND C.Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, transaction.getRT_id());
            ps.setString(2, transaction.getRT_title());
            ps.setString(3, transaction.getRT_type());
            ps.setBigDecimal(4, transaction.getRT_amount());
            ps.setString(5, transaction.getRT_frequency());
            ps.setDate(6, transaction.getRT_start_date());
            ps.setDate(7, transaction.getRT_end_date());
            ps.setDate(8, transaction.getRT_next_date());
            ps.setString(9, transaction.getRT_status());
            ps.setString(10, transaction.getAccounts_A_id());
            ps.setString(11, userId);
            ps.setString(12, transaction.getCategories_C_id());
            ps.setString(13, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ
    public List<RecurringTransaction> getAllRecurringTransactions(
            String userId) {

        List<RecurringTransaction> transactions = new ArrayList<>();

        String sql = "SELECT RT.* FROM Recurring_Transactions RT " +
            "JOIN Accounts A ON RT.Accounts_A_id = A.A_id " +
            "JOIN Categories C ON RT.Categories_C_id = C.C_id " +
            "WHERE A.Users_U_id = ? AND C.Users_U_id = ? " +
            "ORDER BY RT.RT_next_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setString(2, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                RecurringTransaction transaction =
                    new RecurringTransaction(
                        rs.getString("RT_id"),
                        rs.getString("RT_title"),
                        rs.getString("RT_type"),
                        rs.getBigDecimal("RT_amount"),
                        rs.getString("RT_frequency"),
                        rs.getDate("RT_start_date"),
                        rs.getDate("RT_end_date"),
                        rs.getDate("RT_next_date"),
                        rs.getString("RT_status"),
                        rs.getString("Accounts_A_id"),
                        rs.getString("Categories_C_id")
                    );

                transactions.add(transaction);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    // UPDATE
    public boolean updateRecurringTransaction(
            RecurringTransaction transaction, String userId) {

        String sql = "UPDATE Recurring_Transactions RT " +
            "JOIN Accounts Aold ON RT.Accounts_A_id = Aold.A_id " +
            "JOIN Categories Cold ON RT.Categories_C_id = Cold.C_id " +
            "JOIN Accounts Anew ON Anew.A_id = ? " +
            "JOIN Categories Cnew ON Cnew.C_id = ? " +
            "SET RT.RT_title = ?, RT.RT_type = ?, RT.RT_amount = ?, " +
            "RT.RT_frequency = ?, RT.RT_start_date = ?, " +
            "RT.RT_end_date = ?, RT.RT_next_date = ?, " +
            "RT.RT_status = ?, RT.Accounts_A_id = Anew.A_id, " +
            "RT.Categories_C_id = Cnew.C_id " +
            "WHERE RT.RT_id = ? " +
            "AND Aold.Users_U_id = ? AND Cold.Users_U_id = ? " +
            "AND Anew.Users_U_id = ? AND Cnew.Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, transaction.getAccounts_A_id());
            ps.setString(2, transaction.getCategories_C_id());
            ps.setString(3, transaction.getRT_title());
            ps.setString(4, transaction.getRT_type());
            ps.setBigDecimal(5, transaction.getRT_amount());
            ps.setString(6, transaction.getRT_frequency());
            ps.setDate(7, transaction.getRT_start_date());
            ps.setDate(8, transaction.getRT_end_date());
            ps.setDate(9, transaction.getRT_next_date());
            ps.setString(10, transaction.getRT_status());
            ps.setString(11, transaction.getRT_id());
            ps.setString(12, userId);
            ps.setString(13, userId);
            ps.setString(14, userId);
            ps.setString(15, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean deleteRecurringTransaction(
            String transactionId, String userId) {

        String sql = "DELETE RT FROM Recurring_Transactions RT " +
            "JOIN Accounts A ON RT.Accounts_A_id = A.A_id " +
            "JOIN Categories C ON RT.Categories_C_id = C.C_id " +
            "WHERE RT.RT_id = ? " +
            "AND A.Users_U_id = ? AND C.Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, transactionId);
            ps.setString(2, userId);
            ps.setString(3, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
