package com.budgetbuddies.servlet;

import com.budgetbuddies.dao.DebtDAO;
import com.budgetbuddies.dao.DebtPaymentDAO;
import com.budgetbuddies.model.Debt;
import com.budgetbuddies.model.DebtPayment;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "DebtPaymentServlet", urlPatterns = "/debt-payments")
public class DebtPaymentServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/debt-payments.jsp";
    private final DebtPaymentDAO debtPaymentDAO = new DebtPaymentDAO();
    private final DebtDAO debtDAO = new DebtDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String userId = WebUtil.currentUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        WebUtil.moveFlashToRequest(req);

        List<DebtPayment> debtPayments = debtPaymentDAO.getAllDebtPayments(userId);
        List<Debt> debts = debtDAO.getAllDebts(userId);

        Map<String, String> debtNames = new LinkedHashMap<>();
        for (Debt d : debts) {
            debtNames.put(d.getD_id(), d.getD_name());
        }

        req.setAttribute("debtPayments", debtPayments);
        req.setAttribute("debts", debts);
        req.setAttribute("debtNames", debtNames);

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

        resp.sendRedirect(req.getContextPath() + "/debt-payments");
    }

    private void handleAdd(HttpServletRequest req, String userId) {
        DebtPayment dp = new DebtPayment();
        String error = fillAndValidate(req, dp);
        if (error != null) {
            WebUtil.flashError(req, error);
            return;
        }

        for (int i = 0; i < 5; i++) {
            dp.setDP_id(WebUtil.newId("P"));
            if (debtPaymentDAO.addDebtPayment(dp, userId)) {
                WebUtil.flashSuccess(req, "Pembayaran utang/piutang berhasil dicatat.");
                return;
            }
        }
        WebUtil.flashError(req, "Gagal mencatat pembayaran. Pastikan nominal tidak melebihi sisa utang/piutang.");
    }

    private void handleUpdate(HttpServletRequest req, String userId) {
        String id = WebUtil.trim(req.getParameter("DP_id"));
        if (id.isEmpty() || debtPaymentDAO.findById(id, userId) == null) {
            WebUtil.flashError(req, "Data pembayaran tidak ditemukan.");
            return;
        }

        DebtPayment dp = new DebtPayment();
        String error = fillAndValidate(req, dp);
        if (error != null) {
            WebUtil.flashError(req, error);
            return;
        }
        dp.setDP_id(id);

        if (debtPaymentDAO.updateDebtPayment(dp, userId)) {
            WebUtil.flashSuccess(req, "Data pembayaran berhasil diperbarui.");
        } else {
            WebUtil.flashError(req, "Gagal memperbarui pembayaran. Pastikan nominal tidak melebihi sisa utang/piutang.");
        }
    }

    private void handleDelete(HttpServletRequest req, String userId) {
        String id = WebUtil.trim(req.getParameter("DP_id"));
        if (id.isEmpty()) {
            WebUtil.flashError(req, "ID pembayaran tidak valid.");
            return;
        }
        if (debtPaymentDAO.deleteDebtPayment(id, userId)) {
            WebUtil.flashSuccess(req, "Data pembayaran berhasil dihapus.");
        } else {
            WebUtil.flashError(req, "Gagal menghapus pembayaran.");
        }
    }

    private String fillAndValidate(HttpServletRequest req, DebtPayment dp) {
        String debtId = WebUtil.trim(req.getParameter("Debts_D_id"));
        String amountRaw = WebUtil.trim(req.getParameter("DP_amount"));
        String dateRaw = WebUtil.trim(req.getParameter("DP_payment_date"));
        String notes = WebUtil.trim(req.getParameter("DP_notes"));

        if (debtId.isEmpty()) return "Daftar utang/piutang wajib dipilih.";

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountRaw);
        } catch (NumberFormatException e) {
            return "Nominal pembayaran harus berupa angka.";
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return "Nominal pembayaran harus lebih besar dari 0.";
        amount = amount.setScale(2, RoundingMode.HALF_UP);

        LocalDate date;
        try {
            date = LocalDate.parse(dateRaw);
        } catch (DateTimeParseException e) {
            return "Tanggal pembayaran tidak valid.";
        }

        if (notes.length() > 255) return "Catatan maksimal 255 karakter.";

        dp.setDebts_D_id(debtId);
        dp.setDP_amount(amount);
        dp.setDP_payment_date(Date.valueOf(date));
        dp.setDP_notes(notes.isEmpty() ? null : notes);
        return null;
    }
}