package com.budgetbuddies.dao;

import com.budgetbuddies.config.DBConnection;
import com.budgetbuddies.model.Debt;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DebtDAO {

    // CREATE
    public boolean addDebt(Debt debt) {
        String sql = "INSERT INTO Debts " +
            "(D_id, D_name, D_type, D_total_amount, " +
            "D_remaining_amount, D_due_date, D_status, Users_U_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, debt.getD_id());
            ps.setString(2, debt.getD_name());
            ps.setString(3, debt.getD_type());
            ps.setBigDecimal(4, debt.getD_total_amount());
            ps.setBigDecimal(5, debt.getD_remaining_amount());
            ps.setDate(6, debt.getD_due_date());
            ps.setString(7, debt.getD_status());
            ps.setString(8, debt.getUsers_U_id());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ
    public List<Debt> getAllDebts(String userId) {
        List<Debt> debts = new ArrayList<>();

        String sql = "SELECT * FROM Debts " +
            "WHERE Users_U_id = ? ORDER BY D_due_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Debt debt = new Debt(
                    rs.getString("D_id"),
                    rs.getString("D_name"),
                    rs.getString("D_type"),
                    rs.getBigDecimal("D_total_amount"),
                    rs.getBigDecimal("D_remaining_amount"),
                    rs.getDate("D_due_date"),
                    rs.getString("D_status"),
                    rs.getString("Users_U_id")
                );

                debts.add(debt);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return debts;
    }

    // UPDATE
// READ: Menampilkan satu debt berdasarkan ID dan user
public Debt findById(String id, String userId) {
    String sql = "SELECT * FROM Debts " +
                 "WHERE D_id = ? AND Users_U_id = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, id);
        ps.setString(2, userId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Debt(
                    rs.getString("D_id"),
                    rs.getString("D_name"),
                    rs.getString("D_type"),
                    rs.getBigDecimal("D_total_amount"),
                    rs.getBigDecimal("D_remaining_amount"),
                    rs.getDate("D_due_date"),
                    rs.getString("D_status"),
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
    public boolean updateDebt(Debt debt) {
        String checkSql = "SELECT D_total_amount, D_remaining_amount " +
            "FROM Debts WHERE D_id = ? AND Users_U_id = ? FOR UPDATE";

        String paymentSql = "SELECT COALESCE(SUM(DP_amount), 0) " +
            "AS total_paid FROM Debt_Payments " +
            "WHERE Debts_D_id = ?";

        String updateSql = "UPDATE Debts SET " +
            "D_name = ?, D_type = ?, D_total_amount = ?, " +
            "D_remaining_amount = ?, D_due_date = ?, D_status = ? " +
            "WHERE D_id = ? AND Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                BigDecimal totalPaid;

                try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                    ps.setString(1, debt.getD_id());
                    ps.setString(2, debt.getUsers_U_id());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(paymentSql)) {
                    ps.setString(1, debt.getD_id());

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        totalPaid = rs.getBigDecimal("total_paid");
                    }
                }

                BigDecimal newRemaining =
                    debt.getD_total_amount().subtract(totalPaid);

                if (newRemaining.compareTo(BigDecimal.ZERO) < 0 ||
                    debt.getD_total_amount().compareTo(BigDecimal.ZERO) <= 0) {
                    conn.rollback();
                    return false;
                }

                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setString(1, debt.getD_name());
                    ps.setString(2, debt.getD_type());
                    ps.setBigDecimal(3, debt.getD_total_amount());
                    ps.setBigDecimal(4, newRemaining);
                    ps.setDate(5, debt.getD_due_date());
                    ps.setString(6, debt.getD_status());
                    ps.setString(7, debt.getD_id());
                    ps.setString(8, debt.getUsers_U_id());

                    ps.executeUpdate();
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean deleteDebt(String debtId, String userId) {
        String sql = "DELETE FROM Debts " +
            "WHERE D_id = ? AND Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, debtId);
            ps.setString(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
