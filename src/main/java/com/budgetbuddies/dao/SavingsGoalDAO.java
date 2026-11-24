package com.budgetbuddies.dao;

import com.budgetbuddies.config.DBConnection;
import com.budgetbuddies.model.SavingsGoal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SavingsGoalDAO {

    // CREATE
    public boolean addSavingsGoal(SavingsGoal goal) {
        String sql = "INSERT INTO Savings_Goals " +
            "(SG_id, SG_name, SG_target_amount, SG_current_amount, " +
            "SG_target_date, SG_status, Users_U_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, goal.getSG_id());
            ps.setString(2, goal.getSG_name());
            ps.setBigDecimal(3, goal.getSG_target_amount());
            ps.setBigDecimal(4, goal.getSG_current_amount());
            ps.setDate(5, goal.getSG_target_date());
            ps.setString(6, goal.getSG_status());
            ps.setString(7, goal.getUsers_U_id());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ
    public List<SavingsGoal> getAllSavingsGoals(String userId) {
        List<SavingsGoal> goals = new ArrayList<>();

        String sql = "SELECT * FROM Savings_Goals " +
            "WHERE Users_U_id = ? ORDER BY SG_target_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SavingsGoal goal = new SavingsGoal(
                    rs.getString("SG_id"),
                    rs.getString("SG_name"),
                    rs.getBigDecimal("SG_target_amount"),
                    rs.getBigDecimal("SG_current_amount"),
                    rs.getDate("SG_target_date"),
                    rs.getString("SG_status"),
                    rs.getString("Users_U_id")
                );

                goals.add(goal);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return goals;
    }

    // UPDATE
    public boolean updateSavingsGoal(SavingsGoal goal) {
        String sql = "UPDATE Savings_Goals SET " +
            "SG_name = ?, SG_target_amount = ?, " +
            "SG_current_amount = ?, SG_target_date = ?, SG_status = ? " +
            "WHERE SG_id = ? AND Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, goal.getSG_name());
            ps.setBigDecimal(2, goal.getSG_target_amount());
            ps.setBigDecimal(3, goal.getSG_current_amount());
            ps.setDate(4, goal.getSG_target_date());
            ps.setString(5, goal.getSG_status());
            ps.setString(6, goal.getSG_id());
            ps.setString(7, goal.getUsers_U_id());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean deleteSavingsGoal(String goalId, String userId) {
        String sql = "DELETE FROM Savings_Goals " +
            "WHERE SG_id = ? AND Users_U_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, goalId);
            ps.setString(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
