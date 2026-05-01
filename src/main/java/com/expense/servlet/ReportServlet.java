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
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/report")
public class ReportServlet extends HttpServlet {

    private ExpenseStorageJDBC expenseStorage = new ExpenseStorageJDBC();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        User user = (User) session.getAttribute("user");
        String format = req.getParameter("format");
        String monthParam = req.getParameter("month");

        YearMonth yearMonth;
        if (monthParam != null && !monthParam.isEmpty()) {
            yearMonth = YearMonth.parse(monthParam);
        } else {
            yearMonth = YearMonth.now();
        }

        List<Expense> expenses = expenseStorage.getExpensesByMonth(user.getId(), yearMonth.getYear(), yearMonth.getMonthValue());

        Map<String, Double> categoryTotals = new LinkedHashMap<>();
        for (Expense e : expenses) {
            categoryTotals.merge(e.getCategory(), e.getAmount(), Double::sum);
        }

        Map<LocalDate, Double> dailyTotals = new LinkedHashMap<>();
        for (Expense e : expenses) {
            dailyTotals.merge(e.getDate(), e.getAmount(), Double::sum);
        }

        if ("csv".equals(format)) {
            generateCSVReport(resp, yearMonth, categoryTotals, dailyTotals);
            return;
        }

        generateHTMLReport(resp, user, yearMonth, categoryTotals, dailyTotals, expenses);
    }

    private void generateHTMLReport(HttpServletResponse resp, User user, YearMonth yearMonth,
            Map<String, Double> categoryTotals,
            Map<LocalDate, Double> dailyTotals,
            List<Expense> expenses) throws IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        String monthName = yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        String monthParam = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        String categoriesJson = toJsonArray(new ArrayList<>(categoryTotals.keySet()));
        String amountsJson = toJsonArray(new ArrayList<>(categoryTotals.values()));
        String colorsJson = generateChartColors(categoryTotals.size());
        String dailyChartData = toDailyChartData(dailyTotals);

        String avatarLetter = (user.getFullName() != null && !user.getFullName().isEmpty() ? user.getFullName() : user.getUsername()).substring(0, 1).toUpperCase();
        String displayName = user.getFullName() != null && !user.getFullName().isEmpty() ? user.getFullName() : user.getUsername();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("    <meta charset='UTF-8'>");
        out.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("    <title>Report - Expense Manager</title>");
        out.println("    <link rel='stylesheet' href='css/styles.css'>");
        out.println("    <script src='https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js'></script>");
        out.println("</head>");
        out.println("<body>");
        out.println(navbarHTML(user, avatarLetter, displayName, "report"));
        out.println("    <div class='page-container'>");
        out.println("        <div class='page-header'>");
        out.println("            <h1>Monthly Report</h1>");
        out.println("            <p>" + monthName + "</p>");
        out.println("        </div>");

        out.println("        <div class='card-panel'>");
        out.println("            <div style='display:flex; gap:0.75rem; align-items:flex-end; flex-wrap:wrap; margin-bottom:1.5rem;'>");
        out.println("                <form method='get' action='report' style='display:flex; gap:0.5rem; flex:1;'>");
        out.println("                    <input type='month' name='month' value='" + monthParam + "' style='flex:1;'>");
        out.println("                    <button type='submit' class='btn'>Update</button>");
        out.println("                </form>");
        out.println("                <a href='report?month=" + monthParam + "&format=csv' class='btn btn-outline'>Download CSV</a>");
        out.println("            </div>");

        if (expenses.isEmpty()) {
            out.println("            <div class='empty-state'>");
            out.println("                <p>No expenses found for this month.</p>");
            out.println("            </div>");
        } else {
            double totalSpending = categoryTotals.values().stream().mapToDouble(Double::doubleValue).sum();
            out.println("            <div class='dashboard-tiles'>");
            out.println("                <div class='tile'>");
            out.println("                    <div class='tile-header'><div class='tile-icon blue'><svg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2'><line x1='12' y1='1' x2='12' y2='23'/><path d='M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6'/></svg></div></div>");
            out.println("                    <div class='tile-value'>Rs. " + String.format("%.2f", totalSpending) + "</div>");
            out.println("                    <div class='tile-label'>Total Spending</div>");
            out.println("                </div>");
            out.println("                <div class='tile'>");
            out.println("                    <div class='tile-header'><div class='tile-icon green'><svg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2'><path d='M21.21 15.89A10 10 0 1 1 8 2.83'/><path d='M22 12A10 10 0 0 0 12 2v10z'/></svg></div></div>");
            out.println("                    <div class='tile-value'>" + categoryTotals.size() + "</div>");
            out.println("                    <div class='tile-label'>Categories</div>");
            out.println("                </div>");
            out.println("                <div class='tile'>");
            out.println("                    <div class='tile-header'><div class='tile-icon purple'><svg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2'><path d='M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z'/><polyline points='14 2 14 8 20 8'/></svg></div></div>");
            out.println("                    <div class='tile-value'>" + expenses.size() + "</div>");
            out.println("                    <div class='tile-label'>Transactions</div>");
            out.println("                </div>");
            out.println("                <div class='tile'>");
            out.println("                    <div class='tile-header'><div class='tile-icon orange'><svg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2'><circle cx='12' cy='12' r='10'/><polyline points='12 6 12 12 16 14'/></svg></div></div>");
            out.println("                    <div class='tile-value'>Rs. " + String.format("%.2f", (totalSpending / expenses.size())) + "</div>");
            out.println("                    <div class='tile-label'>Avg per Transaction</div>");
            out.println("                </div>");
            out.println("            </div>");

            out.println("            <div class='grid-2' style='margin-top:1.5rem;'>");
            out.println("                <div>");
            out.println("                    <canvas id='categoryChart'></canvas>");
            out.println("                </div>");
            out.println("                <div>");
            out.println("                    <canvas id='dailyChart'></canvas>");
            out.println("                </div>");
            out.println("            </div>");

            out.println("            <div style='margin-top:1.5rem;'>");
            out.println("                <h2 style='font-size:1.1rem; margin-bottom:1rem;'>Category Details</h2>");
            out.println("                <table>");
            out.println("                    <thead><tr><th>Category</th><th>Amount</th><th>Percentage</th></tr></thead>");
            out.println("                    <tbody>");
            categoryTotals.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .forEach(entry -> {
                        double percentage = (entry.getValue() / totalSpending) * 100;
                        out.println("                    <tr>");
                        out.println("                        <td>" + entry.getKey() + "</td>");
                        out.println("                        <td><strong>Rs. " + String.format("%.2f", entry.getValue()) + "</strong></td>");
                        out.println("                        <td>" + String.format("%.1f", percentage) + "%</td>");
                        out.println("                    </tr>");
                    });
            out.println("                    </tbody>");
            out.println("                </table>");
            out.println("            </div>");
        }

        out.println("        </div>");
        out.println("    </div>");

        if (!expenses.isEmpty()) {
            out.println("<script>");
            out.println("document.addEventListener('DOMContentLoaded', function() {");

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
            out.println("            maintainAspectRatio: false,");
            out.println("            plugins: {");
            out.println("                legend: { position: 'bottom' },");
            out.println("                tooltip: {");
            out.println("                    callbacks: {");
            out.println("                        label: function(context) {");
            out.println("                            const label = context.label || '';");
            out.println("                            const value = context.raw || 0;");
            out.println("                            const total = context.dataset.data.reduce((a, b) => a + b, 0);");
            out.println("                            const percentage = Math.round((value / total) * 100);");
            out.println("                            return `${label}: Rs. ${value.toFixed(2)} (${percentage}%)`;");
            out.println("                        }");
            out.println("                    }");
            out.println("                }");
            out.println("            }");
            out.println("        }");
            out.println("    });");

            out.println("    const dailyCtx = document.getElementById('dailyChart').getContext('2d');");
            out.println("    const dailyData = " + dailyChartData + ";");
            out.println("    new Chart(dailyCtx, {");
            out.println("        type: 'bar',");
            out.println("        data: {");
            out.println("            labels: dailyData.days,");
            out.println("            datasets: [{");
            out.println("                label: 'Daily Spending',");
            out.println("                data: dailyData.amounts,");
            out.println("                backgroundColor: '#059669',");
            out.println("                borderWidth: 1");
            out.println("            }]");
            out.println("        },");
            out.println("        options: {");
            out.println("            responsive: true,");
            out.println("            maintainAspectRatio: false,");
            out.println("            scales: {");
            out.println("                y: {");
            out.println("                    beginAtZero: true,");
            out.println("                    ticks: { callback: function(value) { return 'Rs. ' + value.toFixed(2); } }");
            out.println("                }");
            out.println("            },");
            out.println("            plugins: {");
            out.println("                tooltip: {");
            out.println("                    callbacks: {");
            out.println("                        label: function(context) { return 'Rs. ' + context.raw.toFixed(2); }");
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

    private String navbarHTML(User user, String avatarLetter, String displayName, String activePage) {
        return "<nav class='navbar'>" +
                "    <a href='dashboard' class='navbar-brand'>" +
                "        <svg xmlns='http://www.w3.org/2000/svg' width='28' height='28' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2'><rect x='2' y='7' width='20' height='14' rx='2'/><path d='M16 3h-8l-2 4h12z'/></svg>" +
                "        Expense Manager" +
                "    </a>" +
                "    <button class='mobile-toggle' onclick=\"document.querySelector('.navbar-nav').classList.toggle('open')\" aria-label='Toggle menu'>" +
                "        <svg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2'><line x1='3' y1='6' x2='21' y2='6'/><line x1='3' y1='12' x2='21' y2='12'/><line x1='3' y1='18' x2='21' y2='18'/></svg>" +
                "    </button>" +
                "    <ul class='navbar-nav'>" +
                "        <li><a href='dashboard' class='nav-link" + ("dashboard".equals(activePage) ? " active" : "") + "'>Dashboard</a></li>" +
                "        <li><a href='addExpense.html' class='nav-link" + ("addExpense".equals(activePage) ? " active" : "") + "'>Add Expense</a></li>" +
                "        <li><a href='viewExpenses' class='nav-link" + ("viewExpenses".equals(activePage) ? " active" : "") + "'>Expenses</a></li>" +
                "        <li><a href='report' class='nav-link" + ("report".equals(activePage) ? " active" : "") + "'>Reports</a></li>" +
                "    </ul>" +
                "    <div class='navbar-right'>" +
                "        <button class='theme-toggle' onclick='toggleTheme()' aria-label='Toggle dark mode'>" +
                "            <svg id='sunIcon' xmlns='http://www.w3.org/2000/svg' width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2'><circle cx='12' cy='12' r='5'/><line x1='12' y1='1' x2='12' y2='3'/><line x1='12' y1='21' x2='12' y2='23'/><line x1='4.22' y1='4.22' x2='5.64' y2='5.64'/><line x1='18.36' y1='18.36' x2='19.78' y2='19.78'/><line x1='1' y1='12' x2='3' y2='12'/><line x1='21' y1='12' x2='23' y2='12'/><line x1='4.22' y1='19.78' x2='5.64' y2='18.36'/><line x1='18.36' y1='5.64' x2='19.78' y2='4.22'/></svg>" +
                "            <svg id='moonIcon' style='display:none' xmlns='http://www.w3.org/2000/svg' width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2'><path d='M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z'/></svg>" +
                "        </button>" +
                "        <a href='profile' class='nav-user'>" +
                "            <div class='avatar'>" + avatarLetter + "</div>" +
                "            <div class='nav-user-info'>" +
                "                <span class='nav-user-name'>" + displayName + "</span>" +
                "                <span class='nav-user-role'>@" + user.getUsername() + "</span>" +
                "            </div>" +
                "        </a>" +
                "    </div>" +
                "</nav>" +
                "<script>" +
                "function toggleTheme() { const html=document.documentElement; const isDark=html.getAttribute('data-theme')==='dark'; html.setAttribute('data-theme',isDark?'light':'dark'); localStorage.setItem('theme',isDark?'light':'dark'); updateThemeIcons(!isDark); }" +
                "function updateThemeIcons(isDark) { document.getElementById('sunIcon').style.display=isDark?'none':'block'; document.getElementById('moonIcon').style.display=isDark?'block':'none'; }" +
                "(function(){ const saved=localStorage.getItem('theme'); const isDark=saved==='dark'||(!saved&&window.matchMedia('(prefers-color-scheme:dark)').matches); if(isDark)document.documentElement.setAttribute('data-theme','dark'); updateThemeIcons(isDark); })();" +
                "</script>";
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
                .map(obj -> obj instanceof String ? "\"" + escapeJson((String) obj) + "\"" : String.valueOf(obj))
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
        String[] colors = {"'#2563EB'", "'#059669'", "'#DC2626'", "'#7C3AED'", "'#EA580C'",
                "'#0891B2'", "'#4F46E5'", "'#BE185D'", "'#65A30D'", "'#CA8A04'"};
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
