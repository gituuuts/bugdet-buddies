package com.budgetbuddies.servlet;

import com.budgetbuddies.dao.DebtDAO;
import com.budgetbuddies.model.Debt;
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

@WebServlet(name = "DebtServlet", urlPatterns = "/debts")
public class DebtServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/debts.jsp";
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

        List<Debt> debts = debtDAO.getAllDebts(userId);
        req.setAttribute("debts", debts);

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

        resp.sendRedirect(req.getContextPath() + "/debts");
    }

    private void handleAdd(HttpServletRequest req, String userId) {
        Debt d = new Debt();
        String error = fillAndValidateAdd(req, d);
        if (error != null) {
            WebUtil.flashError(req, error);
            return;
        }
        d.setUsers_U_id(userId);

        for (int i = 0; i < 5; i++) {
            d.setD_id(WebUtil.newId("D"));
            if (debtDAO.addDebt(d)) {
                WebUtil.flashSuccess(req, "Data utang/piutang berhasil ditambahkan.");
                return;
            }
        }
        WebUtil.flashError(req, "Gagal menambahkan data utang/piutang.");
    }

    private void handleUpdate(HttpServletRequest req, String userId) {
        String id = WebUtil.trim(req.getParameter("D_id"));
        Debt existing = id.isEmpty() ? null : debtDAO.findById(id, userId);
        if (existing == null) {
            WebUtil.flashError(req, "Data utang/piutang tidak ditemukan.");
            return;
        }

        Debt d = new Debt();
        String error = fillAndValidateUpdate(req, d, existing);
        if (error != null) {
            WebUtil.flashError(req, error);
            return;
        }
        d.setD_id(id);
        d.setUsers_U_id(userId);

        if (debtDAO.updateDebt(d)) {
            WebUtil.flashSuccess(req, "Data utang/piutang berhasil diperbarui.");
        } else {
            WebUtil.flashError(req, "Gagal memperbarui data. Pastikan total nominal tidak lebih kecil dari jumlah yang sudah dibayar.");
        }
    }

    private void handleDelete(HttpServletRequest req, String userId) {
        String id = WebUtil.trim(req.getParameter("D_id"));
        if (id.isEmpty()) {
            WebUtil.flashError(req, "ID utang/piutang tidak valid.");
            return;
        }
        if (debtDAO.deleteDebt(id, userId)) {
            WebUtil.flashSuccess(req, "Data utang/piutang berhasil dihapus.");
        } else {
            WebUtil.flashError(req, "Gagal menghapus data. Hapus riwayat pembayaran terlebih dahulu jika ada.");
        }
    }

    private String fillAndValidateAdd(HttpServletRequest req, Debt d) {
        String name = WebUtil.trim(req.getParameter("D_name"));
        String type = WebUtil.trim(req.getParameter("D_type"));
        String totalRaw = WebUtil.trim(req.getParameter("D_total_amount"));
        String dueRaw = WebUtil.trim(req.getParameter("D_due_date"));
        String status = WebUtil.trim(req.getParameter("D_status"));

        if (name.isEmpty()) return "Nama/keterangan utang atau piutang wajib diisi.";
        if (name.length() > 100) return "Nama maksimal 100 karakter.";
        if (!"Debt".equals(type) && !"Receivable".equals(type)) {
            return "Jenis harus Utang (Debt) atau Piutang (Receivable).";
        }

        BigDecimal totalAmount;
        try {
            totalAmount = new BigDecimal(totalRaw);
        } catch (NumberFormatException e) {
            return "Total nominal harus berupa angka.";
        }
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) return "Total nominal harus lebih besar dari 0.";
        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);

        Date dueDate = null;
        if (!dueRaw.isEmpty()) {
            try {
                dueDate = Date.valueOf(LocalDate.parse(dueRaw));
            } catch (DateTimeParseException e) {
                return "Tanggal jatuh tempo tidak valid.";
            }
        }

        if (status.isEmpty()) status = "Unpaid";
        if (status.length() > 30) return "Status maksimal 30 karakter.";

        d.setD_name(name);
        d.setD_type(type);
        d.setD_total_amount(totalAmount);
        d.setD_remaining_amount(totalAmount);
        d.setD_due_date(dueDate);
        d.setD_status(status);
        return null;
    }

    private String fillAndValidateUpdate(HttpServletRequest req, Debt d, Debt existing) {
        String name = WebUtil.trim(req.getParameter("D_name"));
        String type = WebUtil.trim(req.getParameter("D_type"));
        String totalRaw = WebUtil.trim(req.getParameter("D_total_amount"));
        String dueRaw = WebUtil.trim(req.getParameter("D_due_date"));
        String status = WebUtil.trim(req.getParameter("D_status"));

        if (name.isEmpty()) return "Nama/keterangan utang atau piutang wajib diisi.";
        if (name.length() > 100) return "Nama maksimal 100 karakter.";
        if (!"Debt".equals(type) && !"Receivable".equals(type)) {
            return "Jenis harus Utang (Debt) atau Piutang (Receivable).";
        }

        BigDecimal totalAmount;
        try {
            totalAmount = new BigDecimal(totalRaw);
        } catch (NumberFormatException e) {
            return "Total nominal harus berupa angka.";
        }
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) return "Total nominal harus lebih besar dari 0.";
        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);

        Date dueDate = null;
        if (!dueRaw.isEmpty()) {
            try {
                dueDate = Date.valueOf(LocalDate.parse(dueRaw));
            } catch (DateTimeParseException e) {
                return "Tanggal jatuh tempo tidak valid.";
            }
        }

        if (status.isEmpty()) status = existing.getD_status();
        if (status.length() > 30) return "Status maksimal 30 karakter.";

        d.setD_name(name);
        d.setD_type(type);
        d.setD_total_amount(totalAmount);
        d.setD_remaining_amount(existing.getD_remaining_amount());
        d.setD_due_date(dueDate);
        d.setD_status(status);
        return null;
    }
}