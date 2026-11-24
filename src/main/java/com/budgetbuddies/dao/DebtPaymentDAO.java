package com.budgetbuddies.dao;

import com.budgetbuddies.config.DBConnection;
import com.budgetbuddies.model.DebtPayment;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DebtPaymentDAO {

    // CREATE
    public boolean addDebtPayment(DebtPayment payment, String userId) {
        String checkSql = "SELECT D_remaining_amount FROM Debts " +
            "WHERE D_id = ? AND Users_U_id = ? FOR UPDATE";

        String updateSql = "UPDATE Debts SET D_remaining_amount = ? " +
            "WHERE D_id = ? AND Users_U_id = ?";

        String insertSql = "INSERT INTO Debt_Payments " +
            "(DP_id, DP_amount, DP_payment_date, DP_notes, Debts_D_id) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                BigDecimal remaining;

                try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                    ps.setString(1, payment.getDebts_D_id());
                    ps.setString(2, userId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }

                        remaining = rs.getBigDecimal("D_remaining_amount");
                    }
                }

                if (remaining.compareTo(payment.getDP_amount()) < 0) {
                    conn.rollback();
                    return false;
                }

                BigDecimal newRemaining =
                    remaining.subtract(payment.getDP_amount());

                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setBigDecimal(1, newRemaining);
                    ps.setString(2, payment.getDebts_D_id());
                    ps.setString(3, userId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setString(1, payment.getDP_id());
                    ps.setBigDecimal(2, payment.getDP_amount());
                    ps.setDate(3, payment.getDP_payment_date());
                    ps.setString(4, payment.getDP_notes());
                    ps.setString(5, payment.getDebts_D_id());

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

    // READ
    public List<DebtPayment> getAllDebtPayments(String userId) {
        List<DebtPayment> payments = new ArrayList<>();

        String sql = "SELECT DP.* FROM Debt_Payments DP " +
            "JOIN Debts D ON DP.Debts_D_id = D.D_id " +
            "WHERE D.Users_U_id = ? " +
            "ORDER BY DP.DP_payment_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DebtPayment payment = new DebtPayment(
                        rs.getString("DP_id"),
                        rs.getBigDecimal("DP_amount"),
                        rs.getDate("DP_payment_date"),
                        rs.getString("DP_notes"),
                        rs.getString("Debts_D_id")
                    );

                    payments.add(payment);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payments;
    }

    // UPDATE
    public boolean updateDebtPayment(DebtPayment payment, String userId) {
        String findSql = "SELECT DP.DP_amount, DP.Debts_D_id " +
            "FROM Debt_Payments DP " +
            "JOIN Debts D ON DP.Debts_D_id = D.D_id " +
            "WHERE DP.DP_id = ? AND D.Users_U_id = ? FOR UPDATE";

        String updatePaymentSql = "UPDATE Debt_Payments SET " +
            "DP_amount = ?, DP_payment_date = ?, DP_notes = ?, " +
            "Debts_D_id = ? WHERE DP_id = ?";

        String updateDebtSql = "UPDATE Debts SET D_remaining_amount = ? " +
            "WHERE D_id = ? AND Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                BigDecimal oldAmount;
                String oldDebtId;

                try (PreparedStatement ps = conn.prepareStatement(findSql)) {
                    ps.setString(1, payment.getDP_id());
                    ps.setString(2, userId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }

                        oldAmount = rs.getBigDecimal("DP_amount");
                        oldDebtId = rs.getString("Debts_D_id");
                    }
                }

                BigDecimal oldRemaining =
                    getRemainingAmount(conn, oldDebtId, userId);

                if (oldRemaining == null) {
                    conn.rollback();
                    return false;
                }

                String newDebtId = payment.getDebts_D_id();
                BigDecimal newRemaining;

                if (oldDebtId.equals(newDebtId)) {
                    // Pembayaran tetap pada utang yang sama
                    newRemaining = oldRemaining
                        .add(oldAmount)
                        .subtract(payment.getDP_amount());

                    if (newRemaining.compareTo(BigDecimal.ZERO) < 0) {
                        conn.rollback();
                        return false;
                    }

                    updateDebtBalance(
                        conn, updateDebtSql, newRemaining, oldDebtId, userId
                    );

                } else {
                    // Pembayaran dipindahkan ke utang lain
                    BigDecimal restoredRemaining =
                        oldRemaining.add(oldAmount);

                    updateDebtBalance(
                        conn, updateDebtSql,
                        restoredRemaining, oldDebtId, userId
                    );

                    BigDecimal targetRemaining =
                        getRemainingAmount(conn, newDebtId, userId);

                    if (targetRemaining == null ||
                        targetRemaining.compareTo(payment.getDP_amount()) < 0) {
                        conn.rollback();
                        return false;
                    }

                    newRemaining = targetRemaining.subtract(
                        payment.getDP_amount()
                    );

                    updateDebtBalance(
                        conn, updateDebtSql,
                        newRemaining, newDebtId, userId
                    );
                }

                try (PreparedStatement ps =
                         conn.prepareStatement(updatePaymentSql)) {

                    ps.setBigDecimal(1, payment.getDP_amount());
                    ps.setDate(2, payment.getDP_payment_date());
                    ps.setString(3, payment.getDP_notes());
                    ps.setString(4, newDebtId);
                    ps.setString(5, payment.getDP_id());

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
    public boolean deleteDebtPayment(String paymentId, String userId) {
        String findSql = "SELECT DP.DP_amount, DP.Debts_D_id " +
            "FROM Debt_Payments DP " +
            "JOIN Debts D ON DP.Debts_D_id = D.D_id " +
            "WHERE DP.DP_id = ? AND D.Users_U_id = ? FOR UPDATE";

        String deleteSql = "DELETE FROM Debt_Payments WHERE DP_id = ?";

        String updateSql = "UPDATE Debts SET D_remaining_amount = ? " +
            "WHERE D_id = ? AND Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                BigDecimal amount;
                String debtId;

                try (PreparedStatement ps = conn.prepareStatement(findSql)) {
                    ps.setString(1, paymentId);
                    ps.setString(2, userId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }

                        amount = rs.getBigDecimal("DP_amount");
                        debtId = rs.getString("Debts_D_id");
                    }
                }

                BigDecimal remaining =
                    getRemainingAmount(conn, debtId, userId);

                if (remaining == null) {
                    conn.rollback();
                    return false;
                }

                BigDecimal restoredRemaining = remaining.add(amount);

                updateDebtBalance(
                    conn, updateSql, restoredRemaining, debtId, userId
                );

                try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                    ps.setString(1, paymentId);

                    if (ps.executeUpdate() == 0) {
                        conn.rollback();
                        return false;
                    }
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

    // HELPER: Mengambil sisa utang dan mengunci barisnya
    private BigDecimal getRemainingAmount(
            Connection conn, String debtId, String userId)
            throws SQLException {

        String sql = "SELECT D_remaining_amount FROM Debts " +
            "WHERE D_id = ? AND Users_U_id = ? FOR UPDATE";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, debtId);
            ps.setString(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("D_remaining_amount");
                }
            }
        }

        return null;
    }

    // HELPER: Memperbarui sisa utang
    private void updateDebtBalance(
            Connection conn, String sql,
            BigDecimal remaining, String debtId, String userId)
            throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, remaining);
            ps.setString(2, debtId);
            ps.setString(3, userId);
            ps.executeUpdate();
        }
    }
}
