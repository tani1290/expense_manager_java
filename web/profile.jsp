<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Profile - Expense Manager</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
    <jsp:include page="navbar.jsp"/>

    <div class="page-container">
        <div class="page-header">
            <h1>Profile Settings</h1>
            <p>Manage your account information and preferences</p>
        </div>

        <c:if test="${param.updated == 'true'}">
            <div class="alert alert-success">Profile updated successfully!</div>
        </c:if>
        <c:if test="${param.passwordChanged == 'true'}">
            <div class="alert alert-success">Password changed successfully!</div>
        </c:if>
        <c:if test="${param.passwordError == 'InvalidCurrent'}">
            <div class="alert alert-error">Current password is incorrect.</div>
        </c:if>
        <c:if test="${param.passwordError == 'TooShort'}">
            <div class="alert alert-error">New password must be at least 4 characters.</div>
        </c:if>
        <c:if test="${param.error == 'MissingEmail'}">
            <div class="alert alert-error">Email is required.</div>
        </c:if>
        <c:if test="${param.error == 'UpdateFailed'}">
            <div class="alert alert-error">Failed to update profile. Please try again.</div>
        </c:if>

        <div class="card-panel">
            <div class="profile-header">
                <div class="profile-avatar">${user.fullName != null && !user.fullName.isEmpty() ? user.fullName.substring(0,1).toUpperCase() : user.username.substring(0,1).toUpperCase()}</div>
                <div class="profile-info">
                    <h2>${user.fullName != null && !user.fullName.isEmpty() ? user.fullName : user.username}</h2>
                    <p>@${user.username}</p>
                </div>
            </div>

            <form action="profile" method="post">
                <div class="profile-section">
                    <h3>Personal Information</h3>
                    <div class="grid-2">
                        <div class="form-group">
                            <label for="fullName">Full Name</label>
                            <input type="text" id="fullName" name="fullName" value="${user.fullName != null ? user.fullName : ''}" placeholder="Enter your full name">
                        </div>
                        <div class="form-group">
                            <label for="username">Username</label>
                            <input type="text" id="username" value="${user.username}" disabled style="opacity: 0.6; cursor: not-allowed;">
                        </div>
                    </div>
                    <div class="grid-2">
                        <div class="form-group">
                            <label for="email">Email</label>
                            <input type="email" id="email" name="email" value="${user.email}" required placeholder="your@email.com">
                        </div>
                        <div class="form-group">
                            <label for="phone">Phone</label>
                            <input type="text" id="phone" name="phone" value="${user.phone != null ? user.phone : ''}" placeholder="+91 9876543210">
                        </div>
                    </div>
                </div>

                <div class="profile-section">
                    <h3>Change Password</h3>
                    <p style="color: var(--text-muted); font-size: 0.85rem; margin-bottom: 1rem;">Leave blank if you don't want to change your password.</p>
                    <div class="grid-2">
                        <div class="form-group">
                            <label for="currentPassword">Current Password</label>
                            <input type="password" id="currentPassword" name="currentPassword" placeholder="Enter current password">
                        </div>
                        <div class="form-group">
                            <label for="newPassword">New Password</label>
                            <input type="password" id="newPassword" name="newPassword" placeholder="Enter new password" minlength="4">
                        </div>
                    </div>
                </div>

                <div class="action-bar">
                    <button type="submit" class="btn">Save Changes</button>
                    <a href="dashboard" class="btn btn-outline">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
