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
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/viewExpenses")
public class ViewExpensesServlet extends HttpServlet {

    private ExpenseStorageJDBC expenseStorage = new ExpenseStorageJDBC();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        User user = (User) session.getAttribute("user");
        List<Expense> expenses = expenseStorage.getExpensesByUserId(user.getId());

        String filterType = req.getParameter("filterType");
        String filterValue = req.getParameter("filterValue");

        if (filterType != null && filterValue != null && !filterValue.isEmpty()) {
            if ("date".equals(filterType)) {
                LocalDate filterDate = LocalDate.parse(filterValue);
                expenses = expenses.stream()
                        .filter(e -> e.getDate().equals(filterDate))
                        .collect(Collectors.toList());
            } else if ("category".equals(filterType)) {
                String finalFilterValue = filterValue;
                expenses = expenses.stream()
                        .filter(e -> e.getCategory().equalsIgnoreCase(finalFilterValue))
                        .collect(Collectors.toList());
            } else if ("month".equals(filterType)) {
                int year = Integer.parseInt(filterValue.substring(0, 4));
                int month = Integer.parseInt(filterValue.substring(5, 7));
                int finalYear = year;
                int finalMonth = month;
                expenses = expenses.stream()
                        .filter(e -> e.getDate().getYear() == finalYear && e.getDate().getMonthValue() == finalMonth)
                        .collect(Collectors.toList());
            }
        }

        double total = expenseStorage.getTotalByUserId(user.getId());
        List<String> categories = expenseStorage.getCategoriesByUserId(user.getId());

        req.setAttribute("expenses", expenses);
        req.setAttribute("total", total);
        req.setAttribute("filterType", filterType);
        req.setAttribute("filterValue", filterValue);
        req.setAttribute("categories", categories);
        req.getRequestDispatcher("viewExpenses.jsp").forward(req, resp);
    }
}
