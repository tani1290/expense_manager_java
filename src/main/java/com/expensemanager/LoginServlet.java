package com.expensemanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
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
	                errorMessage = "<div class='error'>Invalid username or password!</div>";
	                break;
	        }
	    }
	    
	    // Get registration success message if exists
	    String registered = request.getParameter("registered");
	    String successMessage = "";
	    if (registered != null && registered.equals("1")) {
	        successMessage = "<div class='success'>Registration successful! Please login.</div>";
	    }

	    // Preserve username on error
	    String usernameValue = request.getParameter("username") != null ? 
	                         request.getParameter("username") : "";

	    out.println("<!DOCTYPE html>");
	    out.println("<html>");
	    out.println("<head>");
	    out.println("    <title>Login | Expense Manager</title>");
	    out.println("    <link href='https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap' rel='stylesheet'>");
	    out.println("    <link rel='stylesheet' href='css/styles.css'>");
	    out.println("</head>");
	    out.println("<body>");
	    out.println("    <div class='login-container'>");
	    out.println("        <h2>Login</h2>");
	    out.println(errorMessage);
	    out.println(successMessage);
	    out.println("        <form method='post' action='login' class='login-form'>");
	    out.println("            <div class='form-group'>");
	    out.println("                <label for='username'>Username</label>");
	    out.println("                <input type='text' id='username' name='username' value='" + usernameValue + "' required>");
	    out.println("            </div>");
	    out.println("            <div class='form-group'>");
	    out.println("                <label for='password'>Password</label>");
	    out.println("                <input type='password' id='password' name='password' required>");
	    out.println("            </div>");
	    out.println("            <button type='submit' class='btn btn-primary'>Login</button>");
	    out.println("        </form>");
	    out.println("        <p class='text-center mt-2'>Don't have an account? <a href='register'>Register here</a></p>");
	    out.println("    </div>");
	    out.println("</body>");
	    out.println("</html>");
	}

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username").trim();
        String password = request.getParameter("password").trim();
        
        try {
            User user = UserStorageJDBC.getUserByUsername(username);
            
            if (user != null && PasswordHasher.verify(password, user.getPassword())) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                response.sendRedirect("viewExpenses");
            } else {
                response.sendRedirect("login?error=1&username=" + username);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login?error=1&username=" + username);
        }
    }
}