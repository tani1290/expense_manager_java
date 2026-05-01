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
    <jsp:include page="navbar.jsp"/>

    <div class="page-container">
        <div class="page-header">
            <h1>Your Expenses</h1>
            <p>View and manage all your expenses</p>
        </div>

        <c:if test="${param.deleted == 'true'}">
            <div class="alert alert-success">Expense deleted successfully!</div>
        </c:if>
        <c:if test="${param.updated == 'true'}">
            <div class="alert alert-success">Expense updated successfully!</div>
        </c:if>
        <c:if test="${param.added == 'true'}">
            <div class="alert alert-success">Expense added successfully!</div>
        </c:if>
        <c:if test="${param.error == 'NotFound'}">
            <div class="alert alert-error">Expense not found.</div>
        </c:if>

        <div class="card-panel">
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
                    <a href="viewExpenses" class="btn btn-outline">Clear</a>
                </c:if>
            </form>

            <c:if test="${empty expenses}">
                <div class="empty-state">
                    <p>No expenses found.</p>
                    <a href="addExpense.jsp" class="btn mt-1">+ Add Expense</a>
                </div>
            </c:if>
            <c:if test="${!empty expenses}">
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
                                <td style="color: var(--text-muted); max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">${expense.description != null && !expense.description.isEmpty() ? expense.description : '-'}</td>
                                <td>
                                    <div style="display: flex; gap: 0.5rem;">
                                        <a href="editExpense?id=${expense.id}" class="btn btn-sm">Edit</a>
                                        <form action="deleteExpense" method="post" style="display:inline;" onsubmit="return confirm('Delete \'${expense.title}\'?')">
                                            <input type="hidden" name="id" value="${expense.id}">
                                            <button type="submit" class="btn btn-sm btn-warning">Delete</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </div>

        <div class="action-bar">
            <a href="addExpense.jsp" class="btn">+ Add Expense</a>
            <a href="report" class="btn btn-success">View Report</a>
            <a href="dashboard" class="btn btn-outline">Back to Dashboard</a>
        </div>
    </div>
</body>
</html>
