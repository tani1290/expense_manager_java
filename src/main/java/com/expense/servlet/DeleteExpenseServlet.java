package com.expense.servlet;

import com.expense.model.User;
import com.expense.storage.ExpenseStorageJDBC;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/deleteExpense")
public class DeleteExpenseServlet extends HttpServlet {

    private ExpenseStorageJDBC expenseStorage = new ExpenseStorageJDBC();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login.html");
            return;
        }

        User user = (User) session.getAttribute("user");
        String idStr = req.getParameter("id");

        try {
            int id = Integer.parseInt(idStr);
            expenseStorage.deleteExpense(id, user.getId());
            resp.sendRedirect("viewExpenses?deleted=true");
        } catch (NumberFormatException e) {
            resp.sendRedirect("viewExpenses?error=InvalidExpense");
        }
    }
}
