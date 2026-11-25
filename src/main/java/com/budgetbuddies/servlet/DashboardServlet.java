package com.budgetbuddies.servlet;

import com.budgetbuddies.dao.AccountDAO;
import com.budgetbuddies.dao.BudgetDAO;
import com.budgetbuddies.dao.CategoryDAO;
import com.budgetbuddies.dao.DebtDAO;
import com.budgetbuddies.dao.SavingsGoalDAO;
import com.budgetbuddies.dao.TransactionDAO;
import com.budgetbuddies.model.Account;
import com.budgetbuddies.model.Budget;
import com.budgetbuddies.model.Category;
import com.budgetbuddies.model.Debt;
import com.budgetbuddies.model.SavingsGoal;
import com.budgetbuddies.model.Transaction;
import com.budgetbuddies.util.WebUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "DashboardServlet", urlPatterns = "/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/dashboard.jsp";

    private final AccountDAO accountDAO = new AccountDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final BudgetDAO budgetDAO = new BudgetDAO();
    private final SavingsGoalDAO savingsGoalDAO = new SavingsGoalDAO();
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

        BigDecimal totalBalance = accountDAO.getTotalBalance(userId);
        BigDecimal totalIncome = transactionDAO.getTotalIncome(userId);
        BigDecimal totalExpense = transactionDAO.getTotalExpense(userId);

        List<Account> accounts = accountDAO.getAllAccounts(userId);
        List<Category> categories = categoryDAO.getAllCategories(userId);
        List<Transaction> transactions = transactionDAO.getAllTransactions(userId);
        List<Budget> budgets = budgetDAO.getAllBudgets(userId);
        List<SavingsGoal> savingsGoals = savingsGoalDAO.getAllSavingsGoals(userId);
        List<Debt> debts = debtDAO.getAllDebts(userId);

        Map<String, String> accountNames = new LinkedHashMap<>();
        for (Account a : accounts) {
            accountNames.put(a.getA_id(), a.getA_name());
        }
        Map<String, String> categoryNames = new LinkedHashMap<>();
        for (Category c : categories) {
            categoryNames.put(c.getC_id(), c.getC_name());
        }

        List<Transaction> recentTransactions = transactions.size() > 5
                ? transactions.subList(0, 5)
                : transactions;

        req.setAttribute("totalBalance", totalBalance);
        req.setAttribute("totalIncome", totalIncome);
        req.setAttribute("totalExpense", totalExpense);
        req.setAttribute("recentTransactions", recentTransactions);
        req.setAttribute("accountNames", accountNames);
        req.setAttribute("categoryNames", categoryNames);
        req.setAttribute("accountCount", accounts.size());
        req.setAttribute("categoryCount", categories.size());
        req.setAttribute("budgetCount", budgets.size());
        req.setAttribute("savingsCount", savingsGoals.size());
        req.setAttribute("debtCount", debts.size());

        req.getRequestDispatcher(VIEW).forward(req, resp);
    }
}