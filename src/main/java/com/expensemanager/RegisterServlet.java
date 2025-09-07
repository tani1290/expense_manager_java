package com.expensemanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        // Get error message if exists
        String error = request.getParameter("error");
        String errorMessage = "";
        if (error != null) {
            switch (error) {
                case "1":
                    errorMessage = "<p class='error'>Passwords do not match!</p>";
                    break;
                case "2":
                    errorMessage = "<p class='error'>Registration failed. Username already exists.</p>";
                    break;
                case "3":
                    errorMessage = "<p class='error'>Username must be at least 4 characters.</p>";
                    break;
                case "4":
                    errorMessage = "<p class='error'>Password must be at least 6 characters.</p>";
                    break;
            }
        }
        
        // Get success message if exists
        String registered = request.getParameter("registered");
        String successMessage = "";
        if (registered != null && registered.equals("1")) {
            successMessage = "<p class='success'>Registration successful! Please login.</p>";
        }

        // Preserve form values on error
        String usernameValue = request.getParameter("username") != null ? 
                             request.getParameter("username") : "";
        String emailValue = request.getParameter("email") != null ? 
                          request.getParameter("email") : "";

        out.println("<html><head><title>Register</title><link rel='stylesheet' href='css/styles.css'></head><body>");
        out.println("<h2>Register</h2>");
        out.println(errorMessage);
        out.println(successMessage);
        out.println("<form method='post' action='register'>");
        out.println("Username: <input type='text' name='username' value='" + usernameValue + "' required><br><br>");
        out.println("Email: <input type='email' name='email' value='" + emailValue + "'><br><br>");
        out.println("Password: <input type='password' name='password' required><br><br>");
        out.println("Confirm Password: <input type='password' name='confirmPassword' required><br><br>");
        out.println("<input type='submit' value='Register'>");
        out.println("</form>");
        out.println("<p>Already have an account? <a href='login'>Login here</a></p>");
        out.println("</body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username").trim();
        String email = request.getParameter("email") != null ? request.getParameter("email").trim() : "";
        String password = request.getParameter("password").trim();
        String confirmPassword = request.getParameter("confirmPassword").trim();
        
        // Input validation
        if (username.length() < 4) {
            response.sendRedirect("register?error=3&username=" + username + "&email=" + email);
            return;
        }
        
        if (password.length() < 6) {
            response.sendRedirect("register?error=4&username=" + username + "&email=" + email);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            response.sendRedirect("register?error=1&username=" + username + "&email=" + email);
            return;
        }
        
        try {
            // Hash the password before storing
            String hashedPassword = PasswordHasher.hash(password);
            
            // Create user with hashed password
            User user = new User(username, hashedPassword, email);
            
            if (UserStorageJDBC.createUser(user)) {
                response.sendRedirect("login?registered=1");
            } else {
                response.sendRedirect("register?error=2&username=" + username + "&email=" + email);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("register?error=2&username=" + username + "&email=" + email);
        }
    }}