package com.budgetbuddies.servlet;

import com.budgetbuddies.dao.AccountDAO;
import com.budgetbuddies.model.Account;
import com.budgetbuddies.util.WebUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AccountServlet", urlPatterns = "/accounts")
public class AccountServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/accounts.jsp";
    private final AccountDAO accountDAO = new AccountDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String userId = WebUtil.currentUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        WebUtil.moveFlashToRequest(req);

        List<Account> accounts = accountDAO.getAllAccounts(userId);
        Map<String, BigDecimal> balances = accountDAO.getAccountBalances(userId);
        BigDecimal totalBalance = accountDAO.getTotalBalance(userId);

        req.setAttribute("accounts", accounts);
        req.setAttribute("balances", balances);
        req.setAttribute("totalBalance", totalBalance);

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

        resp.sendRedirect(req.getContextPath() + "/accounts");
    }

    private void handleAdd(HttpServletRequest req, String userId) {
        Account a = new Account();
        String error = fillAndValidate(req, a);
        if (error != null) {
            WebUtil.flashError(req, error);
            return;
        }
        a.setUsers_U_id(userId);

        for (int i = 0; i < 5; i++) {
            a.setA_id(WebUtil.newId("A"));
            if (accountDAO.addAccount(a)) {
                WebUtil.flashSuccess(req, "Rekening berhasil ditambahkan.");
                return;
            }
        }
        WebUtil.flashError(req, "Gagal menambahkan rekening.");
    }

    private void handleUpdate(HttpServletRequest req, String userId) {
        String id = WebUtil.trim(req.getParameter("A_id"));
        if (id.isEmpty() || accountDAO.findById(id, userId) == null) {
            WebUtil.flashError(req, "Rekening tidak ditemukan.");
            return;
        }

        Account a = new Account();
        String error = fillAndValidate(req, a);
        if (error != null) {
            WebUtil.flashError(req, error);
            return;
        }
        a.setA_id(id);
        a.setUsers_U_id(userId);

        if (accountDAO.updateAccount(a)) {
            WebUtil.flashSuccess(req, "Rekening berhasil diperbarui.");
        } else {
            WebUtil.flashError(req, "Gagal memperbarui rekening.");
        }
    }

    private void handleDelete(HttpServletRequest req, String userId) {
        String id = WebUtil.trim(req.getParameter("A_id"));
        if (id.isEmpty()) {
            WebUtil.flashError(req, "ID rekening tidak valid.");
            return;
        }
        if (accountDAO.deleteAccount(id, userId)) {
            WebUtil.flashSuccess(req, "Rekening berhasil dihapus.");
        } else {
            WebUtil.flashError(req, "Gagal menghapus rekening. Pastikan tidak masih dipakai transaksi atau transaksi berulang.");
        }
    }

    private String fillAndValidate(HttpServletRequest req, Account a) {
        String name = WebUtil.trim(req.getParameter("A_name"));
        String type = WebUtil.trim(req.getParameter("A_account_type"));
        String balanceRaw = WebUtil.trim(req.getParameter("A_initial_balance"));

        if (name.isEmpty()) return "Nama rekening wajib diisi.";
        if (name.length() > 100) return "Nama rekening maksimal 100 karakter.";
        if (type.isEmpty()) return "Jenis rekening wajib diisi.";
        if (type.length() > 50) return "Jenis rekening maksimal 50 karakter.";

        BigDecimal balance;
        try {
            balance = new BigDecimal(balanceRaw);
        } catch (NumberFormatException e) {
            return "Saldo awal harus berupa angka.";
        }
        if (balance.compareTo(BigDecimal.ZERO) < 0) return "Saldo awal tidak boleh kurang dari 0.";
        if (balance.compareTo(new BigDecimal("9999999999999")) > 0) return "Saldo awal terlalu besar.";
        balance = balance.setScale(2, RoundingMode.HALF_UP);

        a.setA_name(name);
        a.setA_account_type(type);
        a.setA_initial_balance(balance);
        return null;
    }
}