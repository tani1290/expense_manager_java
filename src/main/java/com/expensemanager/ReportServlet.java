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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/report")
public class ReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login");
            return;
        }

        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        String monthParam = req.getParameter("month");
        String format = req.getParameter("format"); // "html" or "csv"

        if (monthParam == null || monthParam.length() != 7) {
            monthParam = YearMonth.now().toString();
        }

        try {
            YearMonth yearMonth = YearMonth.parse(monthParam);
            int year = yearMonth.getYear();
            int month = yearMonth.getMonthValue();

            List<Expense> expenses = ExpenseStorageJDBC.getExpensesByMonth(userId, year, month);

            Map<String, Double> categoryTotals = expenses.stream()
                    .collect(Collectors.groupingBy(
                            Expense::getCategory,
                            Collectors.summingDouble(Expense::getAmount)
                    ));

            Map<LocalDate, Double> dailyTotals = expenses.stream()
                    .collect(Collectors.groupingBy(
                            Expense::getDate,
                            Collectors.summingDouble(Expense::getAmount)
                    ));

            if ("csv".equals(format)) {
                generateCSVReport(resp, yearMonth, categoryTotals, dailyTotals);
                return;
            }

            generateHTMLReport(resp, yearMonth, categoryTotals, dailyTotals, expenses);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating report");
        }
    }

    private void generateHTMLReport(HttpServletResponse resp, YearMonth yearMonth,
            Map<String, Double> categoryTotals,
            Map<LocalDate, Double> dailyTotals,
            List<Expense> expenses) throws IOException {
resp.setContentType("text/html");
PrintWriter out = resp.getWriter();

String monthName = yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
String monthParam = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

// Prepare chart data
String categoriesJson = toJsonArray(new ArrayList<>(categoryTotals.keySet()));
String amountsJson = toJsonArray(new ArrayList<>(categoryTotals.values()));
String colorsJson = generateChartColors(categoryTotals.size());
String dailyChartData = toDailyChartData(dailyTotals);

out.println("<!DOCTYPE html>");
out.println("<html>");
out.println("<head>");
out.println("    <title>Expense Report - " + monthName + "</title>");
out.println("    <link href='https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap' rel='stylesheet'>");
out.println("    <link rel='stylesheet' href='css/styles.css'>");
out.println("    <script src='https://cdn.jsdelivr.net/npm/chart.js'></script>");
out.println("</head>");
out.println("<body>");
out.println("    <div class='report-container'>");
out.println("        <aside class='report-sidebar'>");
out.println("            <h2>Filters & Actions</h2>");
out.println("            <form method='get' action='report' class='inline-form'>");
out.println("                <input type='month' name='month' value='" + monthParam + "'>");
out.println("                <button type='submit' class='btn btn-primary'>Update</button>");
out.println("            </form>");
out.println("            <a href='report?month=" + monthParam + "&format=csv' class='btn btn-secondary mt-2'>Download CSV</a>");
out.println("        </aside>");

out.println("        <main class='report-main'>");
out.println("            <div class='report-header'>");
out.println("                <div>");
out.println("                    <h1 class='report-title'>Monthly Report</h1>");
out.println("                    <p class='report-month'>" + monthName + "</p>");
out.println("                </div>");
out.println("            </div>");

if (expenses.isEmpty()) {
out.println("            <div class='empty-state'>");
out.println("                <p>No expenses found for this month.</p>");
out.println("            </div>");
} else {
double totalSpending = categoryTotals.values().stream().mapToDouble(Double::doubleValue).sum();
out.println("            <div class='summary-cards'>");
out.println("                <div class='summary-card'>");
out.println("                    <h3>Total Spending</h3>");
out.println("                    <p>₹" + String.format("%.2f", totalSpending) + "</p>");
out.println("                </div>");
out.println("                <div class='summary-card'>");
out.println("                    <h3>Categories</h3>");
out.println("                    <p>" + categoryTotals.size() + "</p>");
out.println("                </div>");
out.println("                <div class='summary-card'>");
out.println("                    <h3>Transactions</h3>");
out.println("                    <p>" + expenses.size() + "</p>");
out.println("                </div>");
out.println("            </div>");

// Add pie chart for category breakdown
out.println("            <div class='chart-container'>");
out.println("                <h2>Category Breakdown</h2>");
out.println("                <canvas id='categoryChart'></canvas>");
out.println("            </div>");

// Add bar chart for daily spending
out.println("            <div class='chart-container'>");
out.println("                <h2>Daily Spending</h2>");
out.println("                <canvas id='dailyChart'></canvas>");
out.println("            </div>");

out.println("            <div class='category-breakdown'>");
out.println("                <h2>Category Details</h2>");
out.println("                <table class='category-table'>");
out.println("                    <thead><tr><th>Category</th><th>Amount</th><th>Percentage</th></tr></thead>");
out.println("                    <tbody>");
categoryTotals.entrySet().stream()
.sorted(Map.Entry.<String, Double>comparingByValue().reversed())
.forEach(entry -> {
 double percentage = (entry.getValue() / totalSpending) * 100;
 out.println("                    <tr>");
 out.println("                        <td>" + entry.getKey() + "</td>");
 out.println("                        <td>₹" + String.format("%.2f", entry.getValue()) + "</td>");
 out.println("                        <td>" + String.format("%.1f", percentage) + "%</td>");
 out.println("                    </tr>");
});
out.println("                    </tbody>");
out.println("                </table>");
out.println("            </div>");
}

out.println("            <div class='navigation-links'>");
out.println("                <a href='viewExpenses' class='nav-link'>Back to Expenses</a>");
out.println("                <a href='logout' class='nav-link logout'>Logout</a>");
out.println("            </div>");
out.println("        </main>");
out.println("    </div>");

// Add JavaScript to render charts
if (!expenses.isEmpty()) {
out.println("<script>");
out.println("document.addEventListener('DOMContentLoaded', function() {");

// Pie chart for categories
out.println("    const categoryCtx = document.getElementById('categoryChart').getContext('2d');");
out.println("    new Chart(categoryCtx, {");
out.println("        type: 'pie',");
out.println("        data: {");
out.println("            labels: " + categoriesJson + ",");
out.println("            datasets: [{");
out.println("                data: " + amountsJson + ",");
out.println("                backgroundColor: [" + colorsJson + "],");
out.println("                borderWidth: 1");
out.println("            }]");
out.println("        },");
out.println("        options: {");
out.println("            responsive: true,");
out.println("            plugins: {");
out.println("                legend: {");
out.println("                    position: 'right',");
out.println("                },");
out.println("                tooltip: {");
out.println("                    callbacks: {");
out.println("                        label: function(context) {");
out.println("                            const label = context.label || '';");
out.println("                            const value = context.raw || 0;");
out.println("                            const total = context.dataset.data.reduce((a, b) => a + b, 0);");
out.println("                            const percentage = Math.round((value / total) * 100);");
out.println("                            return `${label}: ₹${value.toFixed(2)} (${percentage}%)`;");
out.println("                        }");
out.println("                    }");
out.println("                }");
out.println("            }");
out.println("        }");
out.println("    });");

// Bar chart for daily spending
out.println("    const dailyCtx = document.getElementById('dailyChart').getContext('2d');");
out.println("    const dailyData = " + dailyChartData + ";");
out.println("    new Chart(dailyCtx, {");
out.println("        type: 'bar',");
out.println("        data: {");
out.println("            labels: dailyData.days,");
out.println("            datasets: [{");
out.println("                label: 'Daily Spending',");
out.println("                data: dailyData.amounts,");
out.println("                backgroundColor: '#4CAF50',");
out.println("                borderColor: '#388E3C',");
out.println("                borderWidth: 1");
out.println("            }]");
out.println("        },");
out.println("        options: {");
out.println("            responsive: true,");
out.println("            scales: {");
out.println("                y: {");
out.println("                    beginAtZero: true,");
out.println("                    ticks: {");
out.println("                        callback: function(value) {");
out.println("                            return '₹' + value.toFixed(2);");
out.println("                        }");
out.println("                    }");
out.println("                }");
out.println("            },");
out.println("            plugins: {");
out.println("                tooltip: {");
out.println("                    callbacks: {");
out.println("                        label: function(context) {");
out.println("                            return '₹' + context.raw.toFixed(2);");
out.println("                        }");
out.println("                    }");
out.println("                }");
out.println("            }");
out.println("        }");
out.println("    });");
out.println("});");
out.println("</script>");
}

out.println("</body>");
out.println("</html>");
}
    private void generateCSVReport(HttpServletResponse resp, YearMonth yearMonth,
                                    Map<String, Double> categoryTotals,
                                    Map<LocalDate, Double> dailyTotals) throws IOException {
        resp.setContentType("text/csv");
        resp.setHeader("Content-Disposition", "attachment; filename=expense-report-" + yearMonth + ".csv");

        PrintWriter out = resp.getWriter();
        out.println("Category,Amount");
        categoryTotals.forEach((category, amount) -> out.println("\"" + category + "\"," + amount));

        out.println("\nDate,Amount");
        dailyTotals.forEach((date, amount) -> out.println(date + "," + amount));
    }

    private String toJsonArray(List<?> list) {
        return list.stream()
                .map(obj -> obj instanceof String ?
                        "\"" + escapeJson((String) obj) + "\"" :
                        String.valueOf(obj))
                .collect(Collectors.joining(",", "[", "]"));
    }

    private String escapeJson(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String generateChartColors(int count) {
        String[] colors = {"'#4CAF50'", "'#2196F3'", "'#FFC107'", "'#FF5722'", "'#9C27B0'",
                "'#607D8B'", "'#795548'", "'#3F51B5'", "'#00BCD4'", "'#8BC34A'"};
        List<String> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(colors[i % colors.length]);
        }
        return String.join(",", result);
    }

    private String toDailyChartData(Map<LocalDate, Double> dailyTotals) {
        List<String> days = new ArrayList<>();
        List<Double> amounts = new ArrayList<>();

        LocalDate start = dailyTotals.keySet().stream().min(LocalDate::compareTo).orElse(LocalDate.now());
        LocalDate end = dailyTotals.keySet().stream().max(LocalDate::compareTo).orElse(LocalDate.now());

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            days.add(String.valueOf(date.getDayOfMonth()));
            amounts.add(dailyTotals.getOrDefault(date, 0.0));
        }

        return "{days: " + toJsonArray(days) + ", amounts: " + toJsonArray(amounts) + "}";
    }
}
