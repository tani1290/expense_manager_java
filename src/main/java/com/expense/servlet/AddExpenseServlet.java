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
import java.time.LocalDate;

@WebServlet("/addExpense")
public class AddExpenseServlet extends HttpServlet {

    private ExpenseStorageJDBC expenseStorage = new ExpenseStorageJDBC();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        User user = (User) session.getAttribute("user");

        String title = req.getParameter("title");
        String amountStr = req.getParameter("amount");
        String category = req.getParameter("category");
        String dateStr = req.getParameter("date");
        String description = req.getParameter("description");

        if (title == null || title.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/addExpense.jsp?error=MissingTitle");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                resp.sendRedirect(req.getContextPath() + "/addExpense.jsp?error=InvalidAmount");
                return;
            }
            LocalDate date = LocalDate.parse(dateStr);

            Expense expense = new Expense(user.getId(), title.trim(), amount, category, date, description);
            expenseStorage.addExpense(expense);

            resp.sendRedirect(req.getContextPath() + "/viewExpenses?added=true");
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/addExpense.jsp?error=InvalidInput");
        } catch (IllegalArgumentException e) {
            resp.sendRedirect(req.getContextPath() + "/addExpense.jsp?error=InvalidDate");
        }
    }
}
