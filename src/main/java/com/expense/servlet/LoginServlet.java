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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserStorageJDBC userStorage = new UserStorageJDBC();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/viewExpenses");
        } else {
            req.getRequestDispatcher("login.html").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/login.html?error=MissingFields");
            return;
        }

        if (userStorage.validateUser(username.trim(), password)) {
            User user = userStorage.getUserByUsername(username.trim());
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            resp.sendRedirect(req.getContextPath() + "/viewExpenses");
        } else {
            resp.sendRedirect(req.getContextPath() + "/login.html?error=InvalidCredentials");
        }
    }
}
