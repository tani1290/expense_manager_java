package com.expensemanager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseStorageJDBC {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/expense_tracker?useSSL=false&serverTimezone=UTC";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "Tani@1290";

    public static List<Expense> getExpensesByMonth(int userId, int year, int month) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE user_id = ? AND YEAR(date) = ? AND MONTH(date) = ? ORDER BY date DESC";
        
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, year);
            pstmt.setInt(3, month);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Expense expense = new Expense(
                    rs.getDate("date").toLocalDate(),
                    rs.getString("category"),
                    rs.getDouble("amount"),
                    rs.getString("description")
                );
                expense.setId(rs.getInt("id"));
                expenses.add(expense);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching expenses: " + e.getMessage());
            e.printStackTrace();
        }
        
        return expenses;
    }

    public static List<Expense> getExpenses(int userId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE user_id = ? ORDER BY date DESC";
        
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Expense expense = new Expense(
                    rs.getDate("date").toLocalDate(),
                    rs.getString("category"),
                    rs.getDouble("amount"),
                    rs.getString("description")
                );
                expense.setId(rs.getInt("id"));
                expenses.add(expense);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching expenses: " + e.getMessage());
            e.printStackTrace();
        }
        
        return expenses;
    }

    public static boolean addExpense(Expense expense, int userId) {
        String sql = "INSERT INTO expenses (user_id, date, category, amount, description) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, userId);
            pstmt.setDate(2, Date.valueOf(expense.getDate()));
            pstmt.setString(3, expense.getCategory());
            pstmt.setDouble(4, expense.getAmount());
            pstmt.setString(5, expense.getDescription());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        expense.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error adding expense: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}