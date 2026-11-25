package com.budgetbuddies.servlet;

import com.budgetbuddies.dao.SavingsGoalDAO;
import com.budgetbuddies.model.SavingsGoal;
import com.budgetbuddies.util.WebUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet(name = "SavingsGoalServlet", urlPatterns = "/savings-goals")
public class SavingsGoalServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/savings-goals.jsp";
    private final SavingsGoalDAO savingsGoalDAO = new SavingsGoalDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String userId = WebUtil.currentUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        WebUtil.moveFlashToRequest(req);

        List<SavingsGoal> savingsGoals = savingsGoalDAO.getAllSavingsGoals(userId);
        req.setAttribute("savingsGoals", savingsGoals);

        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String userId = WebUtil.currentUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = WebUtil.trim(req.getParameter("action"));

        switch (action) {
            case "add":
                handleAdd(req, userId);
                break;
            case "update":
                handleUpdate(req, userId);
                break;
            case "delete":
                handleDelete(req, userId);
                break;
            default:
                WebUtil.flashError(req, "Aksi tidak dikenal.");
        }

        resp.sendRedirect(req.getContextPath() + "/savings-goals");
    }

    private void handleAdd(HttpServletRequest req, String userId) {
        SavingsGoal g = new SavingsGoal();
        String error = fillAndValidate(req, g);
        if (error != null) {
            WebUtil.flashError(req, error);
            return;
        }
        g.setUsers_U_id(userId);

        for (int i = 0; i < 5; i++) {
            g.setSG_id(WebUtil.newId("SG"));
            if (savingsGoalDAO.addSavingsGoal(g)) {
                WebUtil.flashSuccess(req, "Target tabungan berhasil ditambahkan.");
                return;
            }
        }
        WebUtil.flashError(req, "Gagal menambahkan target tabungan.");
    }

    private void handleUpdate(HttpServletRequest req, String userId) {
        String id = WebUtil.trim(req.getParameter("SG_id"));
        if (id.isEmpty() || savingsGoalDAO.findById(id, userId) == null) {
            WebUtil.flashError(req, "Target tabungan tidak ditemukan.");
            return;
        }

        SavingsGoal g = new SavingsGoal();
        String error = fillAndValidate(req, g);
        if (error != null) {
            WebUtil.flashError(req, error);
            return;
        }
        g.setSG_id(id);
        g.setUsers_U_id(userId);

        if (savingsGoalDAO.updateSavingsGoal(g)) {
            WebUtil.flashSuccess(req, "Target tabungan berhasil diperbarui.");
        } else {
            WebUtil.flashError(req, "Gagal memperbarui target tabungan.");
        }
    }

    private void handleDelete(HttpServletRequest req, String userId) {
        String id = WebUtil.trim(req.getParameter("SG_id"));
        if (id.isEmpty()) {
            WebUtil.flashError(req, "ID target tabungan tidak valid.");
            return;
        }
        if (savingsGoalDAO.deleteSavingsGoal(id, userId)) {
            WebUtil.flashSuccess(req, "Target tabungan berhasil dihapus.");
        } else {
            WebUtil.flashError(req, "Gagal menghapus target tabungan.");
        }
    }

    private String fillAndValidate(HttpServletRequest req, SavingsGoal g) {
        String name = WebUtil.trim(req.getParameter("SG_name"));
        String targetRaw = WebUtil.trim(req.getParameter("SG_target_amount"));
        String currentRaw = WebUtil.trim(req.getParameter("SG_current_amount"));
        String dateRaw = WebUtil.trim(req.getParameter("SG_target_date"));
        String status = WebUtil.trim(req.getParameter("SG_status"));

        if (name.isEmpty()) return "Nama target tabungan wajib diisi.";
        if (name.length() > 100) return "Nama target tabungan maksimal 100 karakter.";

        BigDecimal targetAmount;
        try {
            targetAmount = new BigDecimal(targetRaw);
        } catch (NumberFormatException e) {
            return "Target nominal harus berupa angka.";
        }
        if (targetAmount.compareTo(BigDecimal.ZERO) <= 0) return "Target nominal harus lebih besar dari 0.";
        targetAmount = targetAmount.setScale(2, RoundingMode.HALF_UP);

        BigDecimal currentAmount;
        try {
            currentAmount = currentRaw.isEmpty() ? BigDecimal.ZERO : new BigDecimal(currentRaw);
        } catch (NumberFormatException e) {
            return "Saldo terkumpul harus berupa angka.";
        }
        if (currentAmount.compareTo(BigDecimal.ZERO) < 0) return "Saldo terkumpul tidak boleh kurang dari 0.";
        currentAmount = currentAmount.setScale(2, RoundingMode.HALF_UP);

        Date targetDate = null;
        if (!dateRaw.isEmpty()) {
            try {
                targetDate = Date.valueOf(LocalDate.parse(dateRaw));
            } catch (DateTimeParseException e) {
                return "Tanggal target tidak valid.";
            }
        }

        if (status.isEmpty()) status = "Active";
        if (status.length() > 30) return "Status maksimal 30 karakter.";

        g.setSG_name(name);
        g.setSG_target_amount(targetAmount);
        g.setSG_current_amount(currentAmount);
        g.setSG_target_date(targetDate);
        g.setSG_status(status);
        return null;
    }
}