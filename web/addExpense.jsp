<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Expense - Expense Manager</title>
    <link rel="stylesheet" href="css/styles.css">
    <script>
        window.onload = function () {
            document.getElementById('date').valueAsDate = new Date();
            const urlParams = new URLSearchParams(window.location.search);
            const error = urlParams.get('error');
            if (error) {
                const msg = document.createElement('div');
                msg.className = 'alert alert-error';
                const errors = {
                    'InvalidInput': 'Please enter valid values for all fields.',
                    'InvalidAmount': 'Amount must be greater than zero.',
                    'InvalidDate': 'Please enter a valid date.',
                    'MissingTitle': 'Please enter a title for the expense.'
                };
                msg.textContent = errors[error] || 'An error occurred. Please try again.';
                document.querySelector('.page-container').prepend(msg);
            }

            const catSelect = document.getElementById('categorySelect');
            const catCustom = document.getElementById('categoryCustom');
            const catHidden = document.getElementById('category');

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
            <h1>Add New Expense</h1>
            <p>Record a new expense</p>
        </div>

        <div class="card-panel" style="max-width: 600px;">
            <form action="addExpense" method="post">
                <div class="form-group">
                    <label for="title">Title</label>
                    <input type="text" id="title" name="title" placeholder="e.g. Lunch, Groceries" required>
                </div>

                <div class="form-group">
                    <label for="amount">Amount (Rs.)</label>
                    <input type="number" id="amount" name="amount" step="0.01" min="0.01" placeholder="0.00" required>
                </div>

                <div class="form-group">
                    <label>Category</label>
                    <select id="categorySelect" style="width: 100%;">
                        <option value="Food">Food</option>
                        <option value="Transport">Transport</option>
                        <option value="Utilities">Utilities</option>
                        <option value="Entertainment">Entertainment</option>
                        <option value="Health">Health</option>
                        <option value="Other">Other</option>
                        <option value="__custom__">+ Create Custom Category</option>
                    </select>
                    <input type="text" id="categoryCustom" placeholder="Enter custom category name" style="width: 100%; margin-top: 0.5rem; display: none;">
                    <input type="hidden" id="category" name="category" value="Food">
                </div>

                <div class="form-group">
                    <label for="date">Date</label>
                    <input type="date" id="date" name="date" required>
                </div>

                <div class="form-group">
                    <label for="description">Description (optional)</label>
                    <input type="text" id="description" name="description" placeholder="Additional notes...">
                </div>

                <div class="action-bar">
                    <button type="submit" class="btn">Save Expense</button>
                    <a href="viewExpenses" class="btn btn-outline">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
