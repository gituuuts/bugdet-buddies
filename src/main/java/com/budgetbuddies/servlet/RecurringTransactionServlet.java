package com.budgetbuddies.servlet;

import com.budgetbuddies.dao.AccountDAO;
import com.budgetbuddies.dao.CategoryDAO;
import com.budgetbuddies.dao.RecurringTransactionDAO;
import com.budgetbuddies.model.Account;
import com.budgetbuddies.model.Category;
import com.budgetbuddies.model.RecurringTransaction;
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

@WebServlet(name = "RecurringTransactionServlet", urlPatterns = "/recurring-transactions")
public class RecurringTransactionServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/recurring-transactions.jsp";
    private static final String TYPE_INCOME = "Income";
    private static final String TYPE_EXPENSE = "Expense";

    private final RecurringTransactionDAO recurringDAO = new RecurringTransactionDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String userId = WebUtil.currentUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        WebUtil.moveFlashToRequest(req);

        List<RecurringTransaction> recurringTransactions = recurringDAO.getAllRecurringTransactions(userId);
        List<Account> accounts = accountDAO.getAllAccounts(userId);
        List<Category> categories = categoryDAO.getAllCategories(userId);

        Map<String, String> accountNames = new LinkedHashMap<>();
        for (Account a : accounts) {
            accountNames.put(a.getA_id(), a.getA_name());
        }
        Map<String, String> categoryNames = new LinkedHashMap<>();
        for (Category c : categories) {
            categoryNames.put(c.getC_id(), c.getC_name());
        }

        req.setAttribute("recurringTransactions", recurringTransactions);
        req.setAttribute("accounts", accounts);
        req.setAttribute("categories", categories);
        req.setAttribute("accountNames", accountNames);
        req.setAttribute("categoryNames", categoryNames);

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

        resp.sendRedirect(req.getContextPath() + "/recurring-transactions");
    }

    private void handleAdd(HttpServletRequest req, String userId) {
        RecurringTransaction rt = new RecurringTransaction();
        String error = fillAndValidate(req, rt);
        if (error != null) {
            WebUtil.flashError(req, error);
            return;
        }

        for (int i = 0; i < 5; i++) {
            rt.setRT_id(WebUtil.newId("R"));
            if (recurringDAO.addRecurringTransaction(rt, userId)) {
                WebUtil.flashSuccess(req, "Transaksi berulang berhasil ditambahkan.");
                return;
            }
        }
        WebUtil.flashError(req, "Gagal menambahkan transaksi berulang. Pastikan akun dan kategori milik Anda.");
    }

    private void handleUpdate(HttpServletRequest req, String userId) {
        String id = WebUtil.trim(req.getParameter("RT_id"));
        if (id.isEmpty() || recurringDAO.findById(id, userId) == null) {
            WebUtil.flashError(req, "Transaksi berulang tidak ditemukan.");
            return;
        }

        RecurringTransaction rt = new RecurringTransaction();
        String error = fillAndValidate(req, rt);
        if (error != null) {
            WebUtil.flashError(req, error);
            return;
        }
        rt.setRT_id(id);

        if (recurringDAO.updateRecurringTransaction(rt, userId)) {
            WebUtil.flashSuccess(req, "Transaksi berulang berhasil diperbarui.");
        } else {
            WebUtil.flashError(req, "Gagal memperbarui transaksi berulang.");
        }
    }

    private void handleDelete(HttpServletRequest req, String userId) {
        String id = WebUtil.trim(req.getParameter("RT_id"));
        if (id.isEmpty()) {
            WebUtil.flashError(req, "ID transaksi berulang tidak valid.");
            return;
        }
        if (recurringDAO.deleteRecurringTransaction(id, userId)) {
            WebUtil.flashSuccess(req, "Transaksi berulang berhasil dihapus.");
        } else {
            WebUtil.flashError(req, "Gagal menghapus transaksi berulang.");
        }
    }

    private String fillAndValidate(HttpServletRequest req, RecurringTransaction rt) {
        String title = WebUtil.trim(req.getParameter("RT_title"));
        String type = WebUtil.trim(req.getParameter("RT_type"));
        String amountRaw = WebUtil.trim(req.getParameter("RT_amount"));
        String frequency = WebUtil.trim(req.getParameter("RT_frequency"));
        String startRaw = WebUtil.trim(req.getParameter("RT_start_date"));
        String endRaw = WebUtil.trim(req.getParameter("RT_end_date"));
        String nextRaw = WebUtil.trim(req.getParameter("RT_next_date"));
        String status = WebUtil.trim(req.getParameter("RT_status"));
        String accountId = WebUtil.trim(req.getParameter("Accounts_A_id"));
        String categoryId = WebUtil.trim(req.getParameter("Categories_C_id"));

        if (title.isEmpty()) return "Judul wajib diisi.";
        if (title.length() > 100) return "Judul maksimal 100 karakter.";
        if (!TYPE_INCOME.equals(type) && !TYPE_EXPENSE.equals(type)) return "Jenis transaksi tidak valid.";

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountRaw);
        } catch (NumberFormatException e) {
            return "Jumlah harus berupa angka.";
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return "Jumlah harus lebih besar dari 0.";
        amount = amount.setScale(2, RoundingMode.HALF_UP);

        if (frequency.isEmpty()) return "Frekuensi wajib dipilih.";
        if (frequency.length() > 30) return "Frekuensi maksimal 30 karakter.";

        LocalDate startDate = parseDate(startRaw);
        if (startDate == null) return "Tanggal mulai wajib diisi dan valid.";

        Date endDate = null;
        if (!endRaw.isEmpty()) {
            LocalDate parsedEnd = parseDate(endRaw);
            if (parsedEnd == null) return "Tanggal selesai tidak valid.";
            if (parsedEnd.isBefore(startDate)) return "Tanggal selesai tidak boleh sebelum tanggal mulai.";
            endDate = Date.valueOf(parsedEnd);
        }

        LocalDate nextDate = parseDate(nextRaw);
        if (nextDate == null) return "Tanggal berikutnya wajib diisi dan valid.";

        if (status.isEmpty()) status = "Active";
        if (status.length() > 30) return "Status maksimal 30 karakter.";

        if (accountId.isEmpty()) return "Akun wajib dipilih.";
        if (categoryId.isEmpty()) return "Kategori wajib dipilih.";

        rt.setRT_title(title);
        rt.setRT_type(type);
        rt.setRT_amount(amount);
        rt.setRT_frequency(frequency);
        rt.setRT_start_date(Date.valueOf(startDate));
        rt.setRT_end_date(endDate);
        rt.setRT_next_date(Date.valueOf(nextDate));
        rt.setRT_status(status);
        rt.setAccounts_A_id(accountId);
        rt.setCategories_C_id(categoryId);
        return null;
    }

    private static LocalDate parseDate(String raw) {
        if (raw == null || raw.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(raw.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}