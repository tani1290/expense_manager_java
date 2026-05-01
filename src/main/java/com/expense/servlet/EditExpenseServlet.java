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

@WebServlet("/editExpense")
public class EditExpenseServlet extends HttpServlet {

    private ExpenseStorageJDBC expenseStorage = new ExpenseStorageJDBC();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        String idStr = req.getParameter("id");
        try {
            int id = Integer.parseInt(idStr);
            User user = (User) session.getAttribute("user");
            Expense expense = expenseStorage.getExpenseById(id);

            if (expense != null && expense.getUserId() == user.getId()) {
                List<String> categories = expenseStorage.getCategoriesByUserId(user.getId());
                req.setAttribute("expense", expense);
                req.setAttribute("categories", categories);
                req.getRequestDispatcher("editExpense.jsp").forward(req, resp);
            } else {
                resp.sendRedirect(req.getContextPath() + "/viewExpenses?error=NotFound");
            }
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/viewExpenses?error=InvalidExpense");
        }
    }
}
