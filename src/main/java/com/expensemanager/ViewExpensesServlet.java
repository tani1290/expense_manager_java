package com.expensemanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/viewExpenses")
public class ViewExpensesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        String filterType = req.getParameter("filterType");
        String filterValue = req.getParameter("filterValue");

        List<Expense> expenses = ExpenseStorageJDBC.getExpenses(userId);

        if ("date".equals(filterType) && filterValue != null) {
            LocalDate filterDate = LocalDate.parse(filterValue);
            expenses = expenses.stream()
                    .filter(e -> e.getDate().equals(filterDate))
                    .collect(Collectors.toList());
        } else if ("category".equals(filterType) && filterValue != null) {
            expenses = expenses.stream()
                    .filter(e -> e.getCategory().equalsIgnoreCase(filterValue))
                    .collect(Collectors.toList());
        } else if ("month".equals(filterType) && filterValue != null) {
            expenses = expenses.stream()
                    .filter(e -> e.getDate().getYear() == Integer.parseInt(filterValue.substring(0, 4)) &&
                            e.getDate().getMonthValue() == Integer.parseInt(filterValue.substring(5, 7)))
                    .collect(Collectors.toList());
        }

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("    <title>View Expenses</title>");
        out.println("    <link rel='stylesheet' href='css/styles.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <div class='container'>");
        out.println("        <h2>Your Expenses</h2>");
        out.println("        <form method='get' action='viewExpenses' class='filter-form'>");
        out.println("            <select name='filterType'>");
        out.println("                <option value=''>-- Filter By --</option>");
        out.println("                <option value='date'>Date</option>");
        out.println("                <option value='category'>Category</option>");
        out.println("                <option value='month'>Month</option>");
        out.println("            </select>");
        out.println("            <input type='text' name='filterValue' placeholder='yyyy-MM-dd or category'>");
        out.println("            <button type='submit' class='btn'>Apply Filter</button>");
        out.println("        </form>");
        out.println("        <table>");
        out.println("            <thead>");
        out.println("                <tr>");
        out.println("                    <th>Date</th>");
        out.println("                    <th>Category</th>");
        out.println("                    <th>Amount</th>");
        out.println("                    <th>Description</th>");
        out.println("                </tr>");
        out.println("            </thead>");
        out.println("            <tbody>");
        
        if (expenses.isEmpty()) {
            out.println("            <tr><td colspan='4'>No expenses found</td></tr>");
        } else {
            expenses.forEach(e -> {
                out.println("            <tr>");
                out.println("                <td>" + e.getDate() + "</td>");
                out.println("                <td>" + e.getCategory() + "</td>");
                out.println("                <td>₹" + String.format("%.2f", e.getAmount()) + "</td>");
                out.println("                <td>" + (e.getDescription() != null ? e.getDescription() : "") + "</td>");
                out.println("            </tr>");
            });
        }
        
        out.println("            </tbody>");
        out.println("        </table>");
        out.println("        <div class='nav-links'>");
        out.println("            <a href='addExpense' class='btn-link'>Add New Expense</a>");
        out.println("            <a href='report' class='btn-link'>View Report</a>");
        out.println("            <a href='logout' class='btn-link'>Logout</a>");
        out.println("        </div>");
        out.println("    </div>");
        out.println("</body>");
        out.println("</html>");
    }
}