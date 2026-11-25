package com.budgetbuddies.servlet;

import com.budgetbuddies.dao.CategoryDAO;
import com.budgetbuddies.model.Category;
import com.budgetbuddies.util.WebUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CategoryServlet", urlPatterns = "/categories")
public class CategoryServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/categories.jsp";
    private static final String TYPE_INCOME = "Income";
    private static final String TYPE_EXPENSE = "Expense";

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

        List<Category> categories = categoryDAO.getAllCategories(userId);
        req.setAttribute("categories", categories);

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

        resp.sendRedirect(req.getContextPath() + "/categories");
    }

    private void handleAdd(HttpServletRequest req, String userId) {
        Category c = new Category();
        String error = fillAndValidate(req, c);
        if (error != null) {
            WebUtil.flashError(req, error);
            return;
        }
        c.setUsers_U_id(userId);

        for (int i = 0; i < 5; i++) {
            c.setC_id(WebUtil.newId("C"));
            if (categoryDAO.addCategory(c)) {
                WebUtil.flashSuccess(req, "Kategori berhasil ditambahkan.");
                return;
            }
        }
        WebUtil.flashError(req, "Gagal menambahkan kategori. Nama dan jenis kategori mungkin sudah ada.");
    }

    private void handleUpdate(HttpServletRequest req, String userId) {
        String id = WebUtil.trim(req.getParameter("C_id"));
        if (id.isEmpty() || categoryDAO.findById(id, userId) == null) {
            WebUtil.flashError(req, "Kategori tidak ditemukan.");
            return;
        }

        Category c = new Category();
        String error = fillAndValidate(req, c);
        if (error != null) {
            WebUtil.flashError(req, error);
            return;
        }
        c.setC_id(id);
        c.setUsers_U_id(userId);

        if (categoryDAO.updateCategory(c)) {
            WebUtil.flashSuccess(req, "Kategori berhasil diperbarui.");
        } else {
            WebUtil.flashError(req, "Gagal memperbarui kategori. Nama dan jenis kategori mungkin duplikat.");
        }
    }

    private void handleDelete(HttpServletRequest req, String userId) {
        String id = WebUtil.trim(req.getParameter("C_id"));
        if (id.isEmpty()) {
            WebUtil.flashError(req, "ID kategori tidak valid.");
            return;
        }
        if (categoryDAO.deleteCategory(id, userId)) {
            WebUtil.flashSuccess(req, "Kategori berhasil dihapus.");
        } else {
            WebUtil.flashError(req, "Gagal menghapus kategori. Kategori mungkin masih digunakan pada transaksi, anggaran, atau transaksi berulang.");
        }
    }

    private String fillAndValidate(HttpServletRequest req, Category c) {
        String name = WebUtil.trim(req.getParameter("C_name"));
        String type = WebUtil.trim(req.getParameter("C_type"));

        if (name.isEmpty()) return "Nama kategori wajib diisi.";
        if (name.length() > 100) return "Nama kategori maksimal 100 karakter.";
        if (!TYPE_INCOME.equals(type) && !TYPE_EXPENSE.equals(type)) {
            return "Jenis kategori harus Pemasukan atau Pengeluaran.";
        }

        c.setC_name(name);
        c.setC_type(type);
        return null;
    }
}