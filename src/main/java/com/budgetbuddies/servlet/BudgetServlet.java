package com.budgetbuddies.servlet;

import com.budgetbuddies.dao.BudgetDAO;
import com.budgetbuddies.dao.CategoryDAO;
import com.budgetbuddies.model.Budget;
import com.budgetbuddies.model.Category;
import com.budgetbuddies.util.WebUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "BudgetServlet", urlPatterns = "/budgets")
public class BudgetServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/budgets.jsp";
    private final BudgetDAO budgetDAO = new BudgetDAO();
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

        List<Budget> budgets = budgetDAO.getAllBudgets(userId);
        List<Category> categories = categoryDAO.getAllCategories(userId);

        Map<String, String> categoryNames = new LinkedHashMap<>();
        for (Category c : categories) {
            categoryNames.put(c.getC_id(), c.getC_name());
        }

        Map<String, BigDecimal> usedAmounts = new LinkedHashMap<>();
        Map<String, BigDecimal> remainingAmounts = new LinkedHashMap<>();
        Map<String, Integer> usagePercents = new LinkedHashMap<>();

        BigDecimal totalLimit = BigDecimal.ZERO;
        BigDecimal totalUsed = BigDecimal.ZERO;

        for (Budget b : budgets) {
            BigDecimal used = budgetDAO.getUsedAmount(userId, b.getCategories_C_id(), b.getB_month(), b.getB_year());
            if (used == null) used = BigDecimal.ZERO;
            BigDecimal remaining = b.getB_amount_limit().subtract(used);

            int pct = 0;
            if (b.getB_amount_limit().compareTo(BigDecimal.ZERO) > 0) {
                pct = used.multiply(BigDecimal.valueOf(100))
                        .divide(b.getB_amount_limit(), 0, RoundingMode.HALF_UP)
                        .intValue();
            }

            usedAmounts.put(b.getB_id(), used);
            remainingAmounts.put(b.getB_id(), remaining);
            usagePercents.put(b.getB_id(), pct);

            totalLimit = totalLimit.add(b.getB_amount_limit());
            totalUsed = totalUsed.add(used);
        }

        req.setAttribute("budgets", budgets);
        req.setAttribute("categories", categories);
        req.setAttribute("categoryNames", categoryNames);
        req.setAttribute("usedAmounts", usedAmounts);
        req.setAttribute("remainingAmounts", remainingAmounts);
        req.setAttribute("usagePercents", usagePercents);
        req.setAttribute("totalLimit", totalLimit);
        req.setAttribute("totalUsed", totalUsed);
        req.setAttribute("totalRemaining", totalLimit.subtract(totalUsed));

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

        resp.sendRedirect(req.getContextPath() + "/budgets");
    }

    private void handleAdd(HttpServletRequest req, String userId) {
        Budget b = new Budget();
        String error = fillAndValidate(req, b);
        if (error != null) {
            WebUtil.flashError(req, error);
            return;
        }
        b.setUsers_U_id(userId);

        for (int i = 0; i < 5; i++) {
            b.setB_id(WebUtil.newId("B"));
            if (budgetDAO.addBudget(b)) {
                WebUtil.flashSuccess(req, "Anggaran berhasil ditambahkan.");
                return;
            }
        }
        WebUtil.flashError(req, "Gagal menambahkan anggaran. Anggaran untuk kategori dan periode yang sama mungkin sudah ada.");
    }

    private void handleUpdate(HttpServletRequest req, String userId) {
        String id = WebUtil.trim(req.getParameter("B_id"));
        if (id.isEmpty() || budgetDAO.findById(id, userId) == null) {
            WebUtil.flashError(req, "Anggaran tidak ditemukan.");
            return;
        }

        Budget b = new Budget();
        String error = fillAndValidate(req, b);
        if (error != null) {
            WebUtil.flashError(req, error);
            return;
        }
        b.setB_id(id);
        b.setUsers_U_id(userId);

        if (budgetDAO.updateBudget(b)) {
            WebUtil.flashSuccess(req, "Anggaran berhasil diperbarui.");
        } else {
            WebUtil.flashError(req, "Gagal memperbarui anggaran. Periksa kategori atau duplikasi periode anggaran.");
        }
    }

    private void handleDelete(HttpServletRequest req, String userId) {
        String id = WebUtil.trim(req.getParameter("B_id"));
        if (id.isEmpty()) {
            WebUtil.flashError(req, "ID anggaran tidak valid.");
            return;
        }
        if (budgetDAO.deleteBudget(id, userId)) {
            WebUtil.flashSuccess(req, "Anggaran berhasil dihapus.");
        } else {
            WebUtil.flashError(req, "Gagal menghapus anggaran.");
        }
    }

    private String fillAndValidate(HttpServletRequest req, Budget b) {
        String categoryId = WebUtil.trim(req.getParameter("Categories_C_id"));
        String limitRaw = WebUtil.trim(req.getParameter("B_amount_limit"));
        String monthRaw = WebUtil.trim(req.getParameter("B_month"));
        String yearRaw = WebUtil.trim(req.getParameter("B_year"));

        if (categoryId.isEmpty()) return "Kategori wajib dipilih.";

        BigDecimal limit;
        try {
            limit = new BigDecimal(limitRaw);
        } catch (NumberFormatException e) {
            return "Batas anggaran harus berupa angka.";
        }
        if (limit.compareTo(BigDecimal.ZERO) <= 0) return "Batas anggaran harus lebih besar dari 0.";
        if (limit.compareTo(new BigDecimal("9999999999999")) > 0) return "Batas anggaran terlalu besar.";
        limit = limit.setScale(2, RoundingMode.HALF_UP);

        int month;
        int year;
        try {
            month = Integer.parseInt(monthRaw);
            year = Integer.parseInt(yearRaw);
        } catch (NumberFormatException e) {
            return "Bulan dan tahun harus berupa angka.";
        }
        if (month < 1 || month > 12) return "Bulan harus antara 1 sampai 12.";
        if (year < 2000 || year > 2100) return "Tahun tidak valid.";

        b.setCategories_C_id(categoryId);
        b.setB_amount_limit(limit);
        b.setB_month(month);
        b.setB_year(year);
        return null;
    }
}