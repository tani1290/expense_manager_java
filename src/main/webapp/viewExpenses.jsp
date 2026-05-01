<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Expenses - Expense Manager</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
    <div class="expenses-table-container">
        <h2>Expense Tracker</h2>

        <c:if test="${param.deleted == 'true'}">
            <div class="success">Expense deleted successfully!</div>
        </c:if>
        <c:if test="${param.updated == 'true'}">
            <div class="success">Expense updated successfully!</div>
        </c:if>
        <c:if test="${param.added == 'true'}">
            <div class="success">Expense added successfully!</div>
        </c:if>
        <c:if test="${param.error == 'NotFound'}">
            <div class="error">Expense not found.</div>
        </c:if>

        <form method="get" action="viewExpenses" class="filter-bar">
            <select name="filterType">
                <option value="">Filter by</option>
                <option value="date" ${filterType == 'date' ? 'selected' : ''}>Date</option>
                <option value="category" ${filterType == 'category' ? 'selected' : ''}>Category</option>
                <option value="month" ${filterType == 'month' ? 'selected' : ''}>Month</option>
            </select>
            <input type="text" name="filterValue" placeholder="Date, category, or month" value="${filterValue}">
            <button type="submit" class="btn">Apply</button>
            <c:if test="${filterType != null && !filterType.isEmpty()}">
                <a href="viewExpenses" class="btn btn-warning">Clear</a>
            </c:if>
        </form>

        <div class="summary-cards">
            <div class="card">
                <h3>Total Spending</h3>
                <p>Rs. <fmt:formatNumber value="${total}" type="number" minFractionDigits="2" maxFractionDigits="2"/></p>
            </div>
            <div class="card">
                <h3>Transactions</h3>
                <p>${fn:length(expenses)}</p>
            </div>
        </div>

        <table>
            <thead>
                <tr>
                    <th>Date</th>
                    <th>Category</th>
                    <th>Amount</th>
                    <th>Title</th>
                    <th>Description</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="expense" items="${expenses}">
                    <tr>
                        <td>${expense.date}</td>
                        <td>${expense.category}</td>
                        <td><strong>Rs. <fmt:formatNumber value="${expense.amount}" type="number" minFractionDigits="2" maxFractionDigits="2"/></strong></td>
                        <td>${expense.title}</td>
                        <td style="color: var(--text-muted);">${expense.description != null && !expense.description.isEmpty() ? expense.description : '-'}</td>
                        <td>
                            <a href="editExpense?id=${expense.id}" class="btn btn-sm">Edit</a>
                            <form action="deleteExpense" method="post" style="display:inline;" onsubmit="return confirm('Delete \'${expense.title}\'?')">
                                <input type="hidden" name="id" value="${expense.id}">
                                <button type="submit" class="btn btn-sm btn-warning">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty expenses}">
                    <tr>
                        <td colspan="6" class="empty-state">No expenses found. Add one to get started!</td>
                    </tr>
                </c:if>
            </tbody>
        </table>

        <div class="nav-links">
            <a href="addExpense.html" class="btn">+ Add Expense</a>
            <a href="report" class="btn btn-success">View Report</a>
            <a href="logout" class="btn btn-warning">Logout</a>
        </div>
    </div>
</body>
</html>
