package com.expense.servlet;

import com.expense.model.Expense;
import com.expense.model.User;
import com.expense.storage.ExpenseStorageJDBC;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private ExpenseStorageJDBC expenseStorage = new ExpenseStorageJDBC();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        double totalSpending = expenseStorage.getTotalByUserId(userId);
        double thisMonthSpending = expenseStorage.getTotalThisMonth(userId);
        double todaySpending = expenseStorage.getTotalToday(userId);
        int expenseCount = expenseStorage.getExpenseCount(userId);
        Map<String, Double> categoryTotals = expenseStorage.getCategoryTotals(userId);
        List<Expense> recentExpenses = expenseStorage.getRecentExpenses(userId, 5);

        String topCategory = categoryTotals.isEmpty() ? "N/A" : categoryTotals.keySet().iterator().next();

        req.setAttribute("user", user);
        req.setAttribute("totalSpending", totalSpending);
        req.setAttribute("thisMonthSpending", thisMonthSpending);
        req.setAttribute("todaySpending", todaySpending);
        req.setAttribute("expenseCount", expenseCount);
        req.setAttribute("categoryTotals", categoryTotals);
        req.setAttribute("recentExpenses", recentExpenses);
        req.setAttribute("topCategory", topCategory);
        req.getRequestDispatcher("dashboard.jsp").forward(req, resp);
    }
}
