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
    <style>
        .filter-input { display: none; }
        .filter-input.active { display: block; }
    </style>
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
            <form method="get" action="viewExpenses" class="filter-bar" id="filterForm">
                <select name="filterType" id="filterType" onchange="updateFilterInput()" style="min-width: 160px;">
                    <option value="">Filter by</option>
                    <option value="date" ${filterType == 'date' ? 'selected' : ''}>Date</option>
                    <option value="month" ${filterType == 'month' ? 'selected' : ''}>Month</option>
                    <option value="category" ${filterType == 'category' ? 'selected' : ''}>Category</option>
                </select>

                <input type="date" name="filterValue" id="filterDate" class="filter-input" value="${filterType == 'date' ? filterValue : ''}">
                <input type="month" name="filterValueMonth" id="filterMonth" class="filter-input" value="${filterType == 'month' ? filterValue : ''}">

                <div id="filterCategoryWrapper" class="filter-input" style="display: flex; gap: 0.5rem; flex: 1; min-width: 150px;">
                    <select id="filterCategorySelect" style="flex: 1;">
                        <option value="">Select category</option>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat}" ${filterType == 'category' && filterValue == cat ? 'selected' : ''}>${cat}</option>
                        </c:forEach>
                        <option value="__custom__">+ Add Custom Category</option>
                    </select>
                    <input type="text" id="filterCategoryCustom" name="filterValue" placeholder="Type category name" style="flex: 1; display: none;">
                    <input type="hidden" id="filterCategoryHidden" name="filterValue" value="${filterType == 'category' ? filterValue : ''}">
                </div>

                <button type="submit" class="btn" style="min-width: 100px;">Apply</button>
                <c:if test="${filterType != null && !filterType.isEmpty()}">
                    <a href="viewExpenses" class="btn btn-outline" style="min-width: 80px;">Clear</a>
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

    <script>
        const DEFAULT_CATEGORIES = ['Food', 'Transport', 'Utilities', 'Entertainment', 'Health', 'Other'];

        function updateFilterInput() {
            const type = document.getElementById('filterType').value;
            document.getElementById('filterDate').classList.remove('active');
            document.getElementById('filterMonth').classList.remove('active');
            document.getElementById('filterCategoryWrapper').classList.remove('active');

            document.getElementById('filterDate').removeAttribute('name');
            document.getElementById('filterMonth').removeAttribute('name');
            document.getElementById('filterCategoryHidden').removeAttribute('name');

            if (type === 'date') {
                document.getElementById('filterDate').classList.add('active');
                document.getElementById('filterDate').setAttribute('name', 'filterValue');
            } else if (type === 'month') {
                document.getElementById('filterMonth').classList.add('active');
                document.getElementById('filterMonth').setAttribute('name', 'filterValue');
            } else if (type === 'category') {
                document.getElementById('filterCategoryWrapper').classList.add('active');
                document.getElementById('filterCategoryHidden').setAttribute('name', 'filterValue');
            }
        }

        const catSelect = document.getElementById('filterCategorySelect');
        const catCustom = document.getElementById('filterCategoryCustom');
        const catHidden = document.getElementById('filterCategoryHidden');

        if (catSelect) {
            catSelect.addEventListener('change', function() {
                if (this.value === '__custom__') {
                    catCustom.style.display = 'block';
                    catCustom.focus();
                    catCustom.addEventListener('input', function() {
                        catHidden.value = this.value;
                    });
                } else {
                    catCustom.style.display = 'none';
                    catHidden.value = this.value;
                }
            });

            const currentVal = catHidden.value;
            if (currentVal && ![...catSelect.options].some(o => o.value === currentVal)) {
                catSelect.value = '__custom__';
                catCustom.style.display = 'block';
                catCustom.value = currentVal;
            }
        }

        updateFilterInput();
    </script>
</body>
</html>
