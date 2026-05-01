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

@WebServlet("/updateExpense")
public class UpdateExpenseServlet extends HttpServlet {

    private ExpenseStorageJDBC expenseStorage = new ExpenseStorageJDBC();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        User user = (User) session.getAttribute("user");

        String idStr = req.getParameter("id");
        String title = req.getParameter("title");
        String amountStr = req.getParameter("amount");
        String category = req.getParameter("category");
        String dateStr = req.getParameter("date");
        String description = req.getParameter("description");

        try {
            int id = Integer.parseInt(idStr);
            double amount = Double.parseDouble(amountStr);
            LocalDate date = LocalDate.parse(dateStr);

            Expense expense = new Expense(id, user.getId(), title, amount, category, date, description);

            if (expenseStorage.updateExpense(expense)) {
                resp.sendRedirect(req.getContextPath() + "/viewExpenses?updated=true");
            } else {
                resp.sendRedirect(req.getContextPath() + "/viewExpenses?error=UpdateFailed");
            }
        } catch (NumberFormatException e) {
            String idParam = req.getParameter("id");
            resp.sendRedirect(req.getContextPath() + "/editExpense?id=" + idParam + "&error=InvalidInput");
        } catch (IllegalArgumentException e) {
            String idParam = req.getParameter("id");
            resp.sendRedirect(req.getContextPath() + "/editExpense?id=" + idParam + "&error=InvalidDate");
        }
    }
}
