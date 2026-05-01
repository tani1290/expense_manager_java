<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Expense Manager</title>
    <link rel="stylesheet" href="css/styles.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
</head>
<body>
    <jsp:include page="navbar.jsp"/>

    <div class="page-container">
        <div class="page-header">
            <h1>Welcome back, ${user.fullName != null && !user.fullName.isEmpty() ? user.fullName : user.username}!</h1>
            <p>Here's an overview of your spending</p>
        </div>

        <c:if test="${param.added == 'true'}">
            <div class="alert alert-success">Expense added successfully!</div>
        </c:if>
        <c:if test="${param.updated == 'true'}">
            <div class="alert alert-success">Expense updated successfully!</div>
        </c:if>
        <c:if test="${param.deleted == 'true'}">
            <div class="alert alert-success">Expense deleted successfully!</div>
        </c:if>

        <div class="dashboard-tiles">
            <div class="tile">
                <div class="tile-header">
                    <div class="tile-icon blue">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
                    </div>
                </div>
                <div class="tile-value">Rs. <fmt:formatNumber value="${totalSpending}" type="number" minFractionDigits="2" maxFractionDigits="2"/></div>
                <div class="tile-label">Total Spending</div>
            </div>

            <div class="tile">
                <div class="tile-header">
                    <div class="tile-icon green">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                    </div>
                    <span class="tile-badge">This Month</span>
                </div>
                <div class="tile-value">Rs. <fmt:formatNumber value="${thisMonthSpending}" type="number" minFractionDigits="2" maxFractionDigits="2"/></div>
                <div class="tile-label">Monthly Spending</div>
            </div>

            <div class="tile">
                <div class="tile-header">
                    <div class="tile-icon purple">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
                    </div>
                </div>
                <div class="tile-value">${expenseCount}</div>
                <div class="tile-label">Total Expenses</div>
            </div>

            <div class="tile">
                <div class="tile-header">
                    <div class="tile-icon orange">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                    </div>
                    <span class="tile-badge">Today</span>
                </div>
                <div class="tile-value">Rs. <fmt:formatNumber value="${todaySpending}" type="number" minFractionDigits="2" maxFractionDigits="2"/></div>
                <div class="tile-label">Today's Spending</div>
            </div>
        </div>

        <div class="grid-2">
            <div class="card-panel">
                <h2>
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21.21 15.89A10 10 0 1 1 8 2.83"/><path d="M22 12A10 10 0 0 0 12 2v10z"/></svg>
                    Category Breakdown
                </h2>
                <canvas id="categoryChart"></canvas>
            </div>

            <div class="card-panel">
                <h2>
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                    Recent Expenses
                </h2>
                <c:if test="${empty recentExpenses}">
                    <div class="empty-state">No expenses yet. <a href="addExpense.jsp">Add one</a> to get started!</div>
                </c:if>
                <c:if test="${!empty recentExpenses}">
                    <table>
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Title</th>
                                <th>Category</th>
                                <th>Amount</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="expense" items="${recentExpenses}">
                                <tr>
                                    <td>${expense.date}</td>
                                    <td>${expense.title}</td>
                                    <td>${expense.category}</td>
                                    <td><strong>Rs. <fmt:formatNumber value="${expense.amount}" type="number" minFractionDigits="2" maxFractionDigits="2"/></strong></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <div class="action-bar">
                        <a href="viewExpenses" class="btn btn-outline">View All Expenses</a>
                    </div>
                </c:if>
            </div>
        </div>
    </div>

    <script>
        const ctx = document.getElementById('categoryChart');
        if (ctx) {
            new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: [
                        <c:forEach var="entry" items="${categoryTotals}" varStatus="status">
                            '${entry.key}'<c:if test="${!status.last}">,</c:if>
                        </c:forEach>
                    ],
                    datasets: [{
                        data: [
                            <c:forEach var="entry" items="${categoryTotals}" varStatus="status">
                                ${entry.value}<c:if test="${!status.last}">,</c:if>
                            </c:forEach>
                        ],
                        backgroundColor: ['#2563EB', '#059669', '#7C3AED', '#EA580C', '#DC2626', '#6B7280'],
                        borderWidth: 0
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { position: 'bottom', labels: { padding: 15, font: { size: 12 } } },
                        tooltip: {
                            callbacks: {
                                label: function(ctx) {
                                    return ' ' + ctx.label + ': Rs. ' + ctx.parsed.toLocaleString('en-IN', {minimumFractionDigits: 2});
                                }
                            }
                        }
                    }
                }
            });
        }
    </script>
</body>
</html>
