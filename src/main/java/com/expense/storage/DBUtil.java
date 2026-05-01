package com.expense.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {

    private static final String URL = "jdbc:h2:./expense_db";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("org.h2.Driver");
            initDB();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static void initDB() {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            String createUsers = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL UNIQUE, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL UNIQUE)";
            stmt.execute(createUsers);

            String createExpenses = "CREATE TABLE IF NOT EXISTS expenses (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT NOT NULL, " +
                    "title VARCHAR(100) NOT NULL, " +
                    "amount DOUBLE NOT NULL, " +
                    "category VARCHAR(50), " +
                    "date DATE NOT NULL, " +
                    "description VARCHAR(500), " +
                    "FOREIGN KEY (user_id) REFERENCES users(id))";
            stmt.execute(createExpenses);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
