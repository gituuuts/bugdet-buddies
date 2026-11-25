package com.budgetbuddies.servlet;

import com.budgetbuddies.dao.AccountDAO;
import com.budgetbuddies.dao.CategoryDAO;
import com.budgetbuddies.dao.TransactionDAO;
import com.budgetbuddies.model.Account;
import com.budgetbuddies.model.Category;
import com.budgetbuddies.model.Transaction;
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
import java.util.stream.Collectors;

/**
 * Sesuai API_CONTRACT.md:
 *   URL  : /transactions   (GET, POST)
 *   POST : action = add | update | delete
 *   Attr : transactions (List<Transaction>)
 *   JSP  : /WEB-INF/views/transactions.jsp
 *
 * Atribut tambahan (perlu diinfokan ke Anggota 1): accounts, categories,
 * accountNames, categoryNames, totalIncome, totalExpense, filter (q/type/from/to).
 */
@WebServlet(name = "TransactionServlet", urlPatterns = "/transactions")
public class TransactionServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/transactions.jsp";
    private static final String TYPE_INCOME = "Income";
    private static final String TYPE_EXPENSE = "Expense";

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    // =====================================================
    // GET  -> action=list (default): tampilkan + cari + filter
    // =====================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String userId = WebUtil.currentUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        WebUtil.moveFlashToRequest(req);

        String q = WebUtil.trim(req.getParameter("q"));
        String type = WebUtil.trim(req.getParameter("type"));
        LocalDate from = parseDate(req.getParameter("from"));
        LocalDate to = parseDate(req.getParameter("to"));

        // Pencarian judul memakai DAO; filter tanggal & jenis dilakukan di sini
        // supaya bisa digabung tanpa mengubah DAO milik Anggota 1.
        List<Transaction> all = q.isEmpty()
                ? transactionDAO.getAllTransactions(userId)
                : transactionDAO.searchByTitle(userId, q);

        List<Transaction> transactions = all.stream()
                .filter(t -> type.isEmpty() || type.equals(t.getT_type()))
                .filter(t -> from == null || !t.getT_transaction_date().toLocalDate().isBefore(from))
                .filter(t -> to == null || !t.getT_transaction_date().toLocalDate().isAfter(to))
                .collect(Collectors.toList());

        BigDecimal totalIncome = sumByType(transactions, TYPE_INCOME);
        BigDecimal totalExpense = sumByType(transactions, TYPE_EXPENSE);

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

        req.setAttribute("transactions", transactions);
        req.setAttribute("accounts", accounts);
        req.setAttribute("categories", categories);
        req.setAttribute("accountNames", accountNames);
        req.setAttribute("categoryNames", categoryNames);
        req.setAttribute("totalIncome", totalIncome);
        req.setAttribute("totalExpense", totalExpense);
        req.setAttribute("filterQ", q);
        req.setAttribute("filterType", type);
        req.setAttribute("filterFrom", from == null ? "" : from.toString());
        req.setAttribute("filterTo", to == null ? "" : to.toString());

        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    // =====================================================
    // POST -> add | update | delete, lalu redirect (PRG)
    // =====================================================
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

        resp.sendRedirect(req.getContextPath() + "/transactions");
    }

    private void handleAdd(HttpServletRequest req, String userId) {
        Transaction t = new Transaction();
        String error = fillAndValidate(req, t);
        if (error != null) {
            WebUtil.flashError(req, error);
            return;
        }

        // ID CHAR(6): coba beberapa kali bila kebetulan bentrok.
        for (int i = 0; i < 5; i++) {
            t.setT_id(WebUtil.newId("T"));
            if (transactionDAO.addTransaction(t, userId)) {
                WebUtil.flashSuccess(req, "Transaksi berhasil ditambahkan.");
                return;
            }
        }
        WebUtil.flashError(req, "Gagal menyimpan transaksi. Pastikan akun dan kategori valid.");
    }

    private void handleUpdate(HttpServletRequest req, String userId) {
        String id = WebUtil.trim(req.getParameter("T_id"));
        if (id.isEmpty() || transactionDAO.findById(id, userId) == null) {
            WebUtil.flashError(req, "Transaksi tidak ditemukan.");
            return;
        }

        Transaction t = new Transaction();
        String error = fillAndValidate(req, t);
        if (error != null) {
            WebUtil.flashError(req, error);
            return;
        }
        t.setT_id(id);

        if (transactionDAO.updateTransaction(t, userId)) {
            WebUtil.flashSuccess(req, "Transaksi berhasil diperbarui.");
        } else {
            WebUtil.flashError(req, "Gagal memperbarui transaksi.");
        }
    }

    private void handleDelete(HttpServletRequest req, String userId) {
        String id = WebUtil.trim(req.getParameter("T_id"));
        if (id.isEmpty()) {
            WebUtil.flashError(req, "ID transaksi tidak valid.");
            return;
        }
        if (transactionDAO.deleteTransaction(id, userId)) {
            WebUtil.flashSuccess(req, "Transaksi berhasil dihapus.");
        } else {
            WebUtil.flashError(req, "Gagal menghapus transaksi.");
        }
    }

    // =====================================================
    // Validasi server-side (client-side hanya pelengkap)
    // Mengembalikan pesan error, atau null jika valid.
    // =====================================================
    private String fillAndValidate(HttpServletRequest req, Transaction t) {
        String title = WebUtil.trim(req.getParameter("T_title"));
        String description = WebUtil.trim(req.getParameter("T_description"));
        String type = WebUtil.trim(req.getParameter("T_type"));
        String amountRaw = WebUtil.trim(req.getParameter("T_amount"));
        String dateRaw = WebUtil.trim(req.getParameter("T_transaction_date"));
        String accountId = WebUtil.trim(req.getParameter("Accounts_A_id"));
        String categoryId = WebUtil.trim(req.getParameter("Categories_C_id"));

        if (title.isEmpty()) return "Judul wajib diisi.";
        if (title.length() > 100) return "Judul maksimal 100 karakter.";
        if (description.length() > 255) return "Deskripsi maksimal 255 karakter.";
        if (!TYPE_INCOME.equals(type) && !TYPE_EXPENSE.equals(type)) return "Jenis transaksi tidak valid.";

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountRaw);
        } catch (NumberFormatException e) {
            return "Jumlah harus berupa angka.";
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return "Jumlah harus lebih besar dari 0.";
        if (amount.compareTo(new BigDecimal("9999999999999")) > 0) return "Jumlah terlalu besar.";
        amount = amount.setScale(2, RoundingMode.HALF_UP);

        LocalDate date = parseDate(dateRaw);
        if (date == null) return "Tanggal tidak valid.";

        if (accountId.isEmpty()) return "Akun wajib dipilih.";
        if (categoryId.isEmpty()) return "Kategori wajib dipilih.";

        t.setT_title(title);
        t.setT_description(description.isEmpty() ? null : description);
        t.setT_type(type);
        t.setT_amount(amount);
        t.setT_transaction_date(Date.valueOf(date));
        t.setAccounts_A_id(accountId);
        t.setCategories_C_id(categoryId);
        return null;
    }

    private static LocalDate parseDate(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(raw.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static BigDecimal sumByType(List<Transaction> list, String type) {
        return list.stream()
                .filter(t -> type.equals(t.getT_type()))
                .map(Transaction::getT_amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
