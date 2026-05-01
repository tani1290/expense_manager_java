<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>View Expenses - Expense Manager</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
    <div class="expenses-table-container">
        <h2>Your Expenses</h2>

        <c:if test="${param.deleted == 'true'}">
            <div class="success">Expense deleted successfully!</div>
        </c:if>
        <c:if test="${param.updated == 'true'}">
            <div class="success">Expense updated successfully!</div>
        </c:if>
        <c:if test="${param.added == 'true'}">
            <div class="success">Expense added successfully!</div>
        </c:if>
        <c:if test="${param.error == 'InvalidExpense'}">
            <div class="error">Invalid expense ID.</div>
        </c:if>
        <c:if test="${param.error == 'UpdateFailed'}">
            <div class="error">Failed to update expense. Please try again.</div>
        </c:if>
        <c:if test="${param.error == 'NotFound'}">
            <div class="error">Expense not found.</div>
        </c:if>

        <form method="get" action="viewExpenses" class="filter-bar">
            <select name="filterType">
                <option value="">-- Filter By --</option>
                <option value="date" ${filterType == 'date' ? 'selected' : ''}>Date</option>
                <option value="category" ${filterType == 'category' ? 'selected' : ''}>Category</option>
                <option value="month" ${filterType == 'month' ? 'selected' : ''}>Month</option>
            </select>
            <input type="text" name="filterValue" placeholder="yyyy-MM-dd or category or yyyy-MM" value="${filterValue}">
            <button type="submit" class="btn">Apply Filter</button>
            <c:if test="${filterType != null && !filterType.isEmpty()}">
                <a href="viewExpenses" class="btn btn-secondary">Clear Filter</a>
            </c:if>
        </form>

        <div class="summary-cards">
            <div class="card">
                <h3>Total Spending</h3>
                <p>$<fmt:formatNumber value="${total}" type="number" minFractionDigits="2" maxFractionDigits="2"/></p>
            </div>
            <div class="card">
                <h3>Showing</h3>
                <p>${fn:length(expenses)} expense(s)</p>
            </div>
        </div>

        <table class="expenses-table">
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
                        <td>$<fmt:formatNumber value="${expense.amount}" type="number" minFractionDigits="2" maxFractionDigits="2"/></td>
                        <td>${expense.title}</td>
                        <td>${expense.description != null ? expense.description : ''}</td>
                        <td>
                            <a href="editExpense?id=${expense.id}" class="btn btn-link">Edit</a>
                            <form action="deleteExpense" method="post" style="display:inline;" onsubmit="return confirmDelete(${expense.id}, '${expense.title}')">
                                <input type="hidden" name="id" value="${expense.id}">
                                <button type="submit" class="btn btn-warn btn-link">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty expenses}">
                    <tr>
                        <td colspan="6" class="empty-state">No expenses found. Start by adding one!</td>
                    </tr>
                </c:if>
            </tbody>
        </table>

        <div class="nav-links">
            <a href="addExpense.html" class="btn btn-primary">Add New Expense</a>
            <a href="report" class="btn btn-secondary">View Report</a>
            <a href="logout" class="btn btn-warn">Logout</a>
        </div>
    </div>

    <script>
        function confirmDelete(id, title) {
            return confirm('Are you sure you want to delete "' + title + '"?');
        }
    </script>
</body>
</html>
