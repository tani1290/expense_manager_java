<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Expense - Expense Manager</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
    <div class="container">
        <h2>Edit Expense</h2>

        <c:if test="${param.error == 'InvalidInput'}">
            <div class="error">Please enter valid values for all fields.</div>
        </c:if>
        <c:if test="${param.error == 'InvalidDate'}">
            <div class="error">Please enter a valid date.</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/updateExpense" method="post" class="login-form">
            <input type="hidden" name="id" value="${expense.id}">

            <div class="form-group">
                <label for="title">Title</label>
                <input type="text" id="title" name="title" value="${expense.title}" required>
            </div>

            <div class="form-group">
                <label for="amount">Amount ($)</label>
                <input type="number" id="amount" name="amount" value="${expense.amount}" step="0.01" min="0.01" required>
            </div>

            <div class="form-group">
                <label for="category">Category</label>
                <select id="category" name="category">
                    <option value="Food" ${expense.category == 'Food' ? 'selected' : ''}>Food</option>
                    <option value="Transport" ${expense.category == 'Transport' ? 'selected' : ''}>Transport</option>
                    <option value="Utilities" ${expense.category == 'Utilities' ? 'selected' : ''}>Utilities</option>
                    <option value="Entertainment" ${expense.category == 'Entertainment' ? 'selected' : ''}>Entertainment</option>
                    <option value="Health" ${expense.category == 'Health' ? 'selected' : ''}>Health</option>
                    <option value="Other" ${expense.category == 'Other' ? 'selected' : ''}>Other</option>
                </select>
            </div>

            <div class="form-group">
                <label for="date">Date</label>
                <input type="date" id="date" name="date" value="${expense.date}" required>
            </div>

            <div class="form-group">
                <label for="description">Description (optional)</label>
                <input type="text" id="description" name="description" value="${expense.description != null ? expense.description : ''}" placeholder="Additional notes...">
            </div>

            <div class="form-group">
                <button type="submit" class="btn btn-block">Update Expense</button>
                <a href="${pageContext.request.contextPath}/viewExpenses" class="btn btn-secondary btn-block mt-1">Cancel</a>
            </div>
        </form>
    </div>
</body>
</html>
