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
import java.time.format.DateTimeFormatter;

@WebServlet("/addExpense")
public class AddExpenseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login");
            return;
        }

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        
        // Get today's date in yyyy-MM-dd format for the date input
        String todayDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("    <title>Add Expense</title>");
        out.println("    <link rel='stylesheet' href='css/styles.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <div class='container'>");
        out.println("        <h2>Add New Expense</h2>");
        
        // Display error message if present
        String error = req.getParameter("error");
        if (error != null) {
            out.println("        <div class='error-message'>");
            if (error.equals("1")) {
                out.println("            <p>Please fill in all required fields.</p>");
            } else if (error.equals("2")) {
                out.println("            <p>Error adding expense. Please try again.</p>");
            }
            out.println("        </div>");
        }
        
        out.println("        <form method='post' action='addExpense'>");
        out.println("            <div class='form-group'>");
        out.println("                <label>Date:</label>");
        out.println("                <input type='date' name='date' value='" + todayDate + "' required>");
        out.println("            </div>");
        out.println("            <div class='form-group'>");
        out.println("                <label>Category:</label>");
        out.println("                <input type='text' name='category' required>");
        out.println("            </div>");
        out.println("            <div class='form-group'>");
        out.println("                <label>Amount (₹):</label>");
        out.println("                <input type='number' step='0.01' min='0' name='amount' required>");
        out.println("            </div>");
        out.println("            <div class='form-group'>");
        out.println("                <label>Description:</label>");
        out.println("                <textarea name='description' rows='3'></textarea>");
        out.println("            </div>");
        out.println("            <div class='form-actions'>");
        out.println("                <button type='submit' class='btn'>Add Expense</button>");
        out.println("                <a href='viewExpenses' class='btn btn-secondary'>Cancel</a>");
        out.println("            </div>");
        out.println("        </form>");
        out.println("    </div>");
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login");
            return;
        }
        
        // Get the user object from session and then get the userId
        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        String dateStr = req.getParameter("date");
        String category = req.getParameter("category");
        String amountStr = req.getParameter("amount");
        String description = req.getParameter("description");

        if (dateStr == null || category == null || amountStr == null) {
            resp.sendRedirect("addExpense?error=1");
            return;
        }

        try {
            LocalDate date = LocalDate.parse(dateStr);
            double amount = Double.parseDouble(amountStr);
            Expense expense = new Expense(date, category, amount, description == null ? "" : description);
            ExpenseStorageJDBC.addExpense(expense, userId);
            resp.sendRedirect("viewExpenses");
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging
            resp.sendRedirect("addExpense?error=2");
        }
    }
}