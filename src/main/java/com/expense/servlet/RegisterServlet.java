package com.expense.servlet;

import com.expense.model.User;
import com.expense.storage.UserStorageJDBC;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.regex.Pattern;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private UserStorageJDBC userStorage = new UserStorageJDBC();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("register.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (username == null || email == null || password == null) {
            resp.sendRedirect(req.getContextPath() + "/register.html?error=MissingFields");
            return;
        }

        username = username.trim();
        email = email.trim();

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            resp.sendRedirect(req.getContextPath() + "/register.html?error=InvalidUsername");
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            resp.sendRedirect(req.getContextPath() + "/register.html?error=InvalidEmail");
            return;
        }

        if (password.length() < 4) {
            resp.sendRedirect(req.getContextPath() + "/register.html?error=PasswordTooShort");
            return;
        }

        User existingUser = userStorage.getUserByUsername(username);
        if (existingUser != null) {
            resp.sendRedirect(req.getContextPath() + "/register.html?error=UserExists");
            return;
        }

        User user = new User(username, password, email);

        if (userStorage.addUser(user)) {
            resp.sendRedirect(req.getContextPath() + "/login.html?registered=true");
        } else {
            resp.sendRedirect(req.getContextPath() + "/register.html?error=RegistrationFailed");
        }
    }
}
