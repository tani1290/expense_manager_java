package com.expense.storage;

import com.expense.model.Expense;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseStorageJDBC {

    public boolean addExpense(Expense expense) {
        String sql = "INSERT INTO expenses (user_id, title, amount, category, date, description) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, expense.getUserId());
            pstmt.setString(2, expense.getTitle());
            pstmt.setDouble(3, expense.getAmount());
            pstmt.setString(4, expense.getCategory());
            pstmt.setDate(5, Date.valueOf(expense.getDate()));
            pstmt.setString(6, expense.getDescription());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Expense getExpenseById(int id) {
        String sql = "SELECT * FROM expenses WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapExpense(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Expense> getExpensesByUserId(int userId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE user_id = ? ORDER BY date DESC";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapExpense(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    public List<Expense> getExpenses(int userId) {
        return getExpensesByUserId(userId);
    }

    public boolean updateExpense(Expense expense) {
        String sql = "UPDATE expenses SET title = ?, amount = ?, category = ?, date = ?, description = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, expense.getTitle());
            pstmt.setDouble(2, expense.getAmount());
            pstmt.setString(3, expense.getCategory());
            pstmt.setDate(4, Date.valueOf(expense.getDate()));
            pstmt.setString(5, expense.getDescription());
            pstmt.setInt(6, expense.getId());
            pstmt.setInt(7, expense.getUserId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteExpense(int id, int userId) {
        String sql = "DELETE FROM expenses WHERE id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getTotalByUserId(int userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM expenses WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public List<Expense> getExpensesByMonth(int userId, int year, int month) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE user_id = ? AND YEAR(date) = ? AND MONTH(date) = ? ORDER BY date DESC";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, year);
            pstmt.setInt(3, month);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapExpense(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    private Expense mapExpense(ResultSet rs) throws SQLException {
        Expense expense = new Expense();
        expense.setId(rs.getInt("id"));
        expense.setUserId(rs.getInt("user_id"));
        expense.setTitle(rs.getString("title"));
        expense.setAmount(rs.getDouble("amount"));
        expense.setCategory(rs.getString("category"));
        Date sqlDate = rs.getDate("date");
        expense.setDate(sqlDate != null ? sqlDate.toLocalDate() : LocalDate.now());
        expense.setDescription(rs.getString("description"));
        return expense;
    }
}
