<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Expense - Expense Manager</title>
    <link rel="stylesheet" href="css/styles.css">
    <script>
        window.onload = function() {
            const catSelect = document.getElementById('categorySelect');
            const catCustom = document.getElementById('categoryCustom');
            const catHidden = document.getElementById('category');
            const currentCat = catHidden.value;

            const isDefault = ['Food','Transport','Utilities','Entertainment','Health','Other'].includes(currentCat);
            if (!isDefault) {
                catSelect.value = '__custom__';
                catCustom.style.display = 'block';
                catCustom.value = currentCat;
            }

            catSelect.addEventListener('change', function() {
                if (this.value === '__custom__') {
                    catCustom.style.display = 'block';
                    catCustom.focus();
                } else {
                    catCustom.style.display = 'none';
                    catHidden.value = this.value;
                }
            });

            catCustom.addEventListener('input', function() {
                catHidden.value = this.value;
            });
        }
    </script>
</head>
<body>
    <jsp:include page="navbar.jsp"/>

    <div class="page-container">
        <div class="page-header">
            <h1>Edit Expense</h1>
            <p>Update expense details</p>
        </div>

        <c:if test="${param.error == 'InvalidInput' || param.error == 'InvalidDate'}">
            <div class="alert alert-error">Please check your input and try again.</div>
        </c:if>

        <div class="card-panel" style="max-width: 600px;">
            <form action="updateExpense" method="post">
                <input type="hidden" name="id" value="${expense.id}">

                <div class="form-group">
                    <label for="title">Title</label>
                    <input type="text" id="title" name="title" value="${expense.title}" required>
                </div>

                <div class="form-group">
                    <label for="amount">Amount (Rs.)</label>
                    <input type="number" id="amount" name="amount" value="${expense.amount}" step="0.01" min="0.01" required>
                </div>

                <div class="form-group">
                    <label>Category</label>
                    <select id="categorySelect" style="width: 100%;">
                        <option value="Food" ${expense.category == 'Food' ? 'selected' : ''}>Food</option>
                        <option value="Transport" ${expense.category == 'Transport' ? 'selected' : ''}>Transport</option>
                        <option value="Utilities" ${expense.category == 'Utilities' ? 'selected' : ''}>Utilities</option>
                        <option value="Entertainment" ${expense.category == 'Entertainment' ? 'selected' : ''}>Entertainment</option>
                        <option value="Health" ${expense.category == 'Health' ? 'selected' : ''}>Health</option>
                        <option value="Other" ${expense.category == 'Other' ? 'selected' : ''}>Other</option>
                        <c:forEach var="cat" items="${categories}">
                            <c:if test="${cat != 'Food' && cat != 'Transport' && cat != 'Utilities' && cat != 'Entertainment' && cat != 'Health' && cat != 'Other'}">
                                <option value="${cat}" ${expense.category == cat ? 'selected' : ''}>${cat}</option>
                            </c:if>
                        </c:forEach>
                        <option value="__custom__">+ Create Custom Category</option>
                    </select>
                    <input type="text" id="categoryCustom" placeholder="Enter custom category name" style="width: 100%; margin-top: 0.5rem; display: none;">
                    <input type="hidden" id="category" name="category" value="${expense.category}">
                </div>

                <div class="form-group">
                    <label for="date">Date</label>
                    <input type="date" id="date" name="date" value="${expense.date}" required>
                </div>

                <div class="form-group">
                    <label for="description">Description (optional)</label>
                    <input type="text" id="description" name="description" value="${expense.description != null ? expense.description : ''}" placeholder="Additional notes...">
                </div>

                <div class="action-bar">
                    <button type="submit" class="btn">Update Expense</button>
                    <a href="viewExpenses" class="btn btn-outline">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
