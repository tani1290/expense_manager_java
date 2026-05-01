package com.expense.servlet;

import com.expense.model.User;
import com.expense.storage.UserStorageJDBC;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private UserStorageJDBC userStorage = new UserStorageJDBC();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        User user = (User) session.getAttribute("user");
        User latestUser = userStorage.getUserById(user.getId());
        if (latestUser != null) {
            session.setAttribute("user", latestUser);
        }

        req.setAttribute("user", latestUser != null ? latestUser : user);
        req.getRequestDispatcher("profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        User user = (User) session.getAttribute("user");

        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String currentPassword = req.getParameter("currentPassword");
        String newPassword = req.getParameter("newPassword");

        if (email == null || email.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/profile?error=MissingEmail");
            return;
        }

        user.setFullName(fullName != null ? fullName.trim() : "");
        user.setEmail(email.trim());
        user.setPhone(phone != null ? phone.trim() : "");

        boolean profileUpdated = userStorage.updateProfile(user);

        if (currentPassword != null && !currentPassword.isEmpty() && newPassword != null && !newPassword.isEmpty()) {
            if (userStorage.validateUser(user.getUsername(), currentPassword)) {
                if (newPassword.length() >= 4) {
                    userStorage.changePassword(user.getId(), newPassword);
                    resp.sendRedirect(req.getContextPath() + "/profile?updated=true&passwordChanged=true");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/profile?updated=true&passwordError=TooShort");
                }
            } else {
                resp.sendRedirect(req.getContextPath() + "/profile?updated=true&passwordError=InvalidCurrent");
            }
        } else if (profileUpdated) {
            User updatedUser = userStorage.getUserById(user.getId());
            if (updatedUser != null) {
                session.setAttribute("user", updatedUser);
            }
            resp.sendRedirect(req.getContextPath() + "/profile?updated=true");
        } else {
            resp.sendRedirect(req.getContextPath() + "/profile?error=UpdateFailed");
        }
    }
}
