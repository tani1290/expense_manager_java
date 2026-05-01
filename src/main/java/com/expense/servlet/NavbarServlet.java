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

@WebServlet("/navbar")
public class NavbarServlet extends HttpServlet {

    private UserStorageJDBC userStorage = new UserStorageJDBC();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            User latest = userStorage.getUserById(user.getId());
            if (latest != null) {
                req.setAttribute("user", latest);
            } else {
                req.setAttribute("user", user);
            }
        }
        req.getRequestDispatcher("navbar.jsp").forward(req, resp);
    }
}
