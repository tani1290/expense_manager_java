package com.expense.storage;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
            seedDemoData();
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
                    "email VARCHAR(100) NOT NULL UNIQUE, " +
                    "full_name VARCHAR(100), " +
                    "phone VARCHAR(20))";
            stmt.execute(createUsers);

            try {
                stmt.execute("ALTER TABLE users ADD COLUMN full_name VARCHAR(100)");
            } catch (SQLException ignored) {}
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN phone VARCHAR(20)");
            } catch (SQLException ignored) {}

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

    private static void seedDemoData() {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM users");
            rs.next();
            if (rs.getInt("cnt") > 0) return;

            String hashedPassword = BCrypt.hashpw("demo123", BCrypt.gensalt());

            stmt.execute("INSERT INTO users (username, password, email, full_name, phone) VALUES " +
                    "('demo', '" + hashedPassword + "', 'demo@expensetracker.com', 'Demo User', '+91 9876543210'), " +
                    "('john', '" + hashedPassword + "', 'john@example.com', 'John Doe', '+91 9876543211')");

            stmt.execute("INSERT INTO expenses (user_id, title, amount, category, date, description) VALUES " +
                    "(1, 'Grocery Shopping', 2500.00, 'Food', '2026-05-01', 'Monthly grocery run at Big Bazaar'), " +
                    "(1, 'Office Lunch', 450.00, 'Food', '2026-05-02', 'Team lunch at nearby restaurant'), " +
                    "(1, 'Morning Coffee', 180.00, 'Food', '2026-05-03', 'Cappuccino from CCD'), " +
                    "(1, 'Dinner with Friends', 1200.00, 'Food', '2026-05-05', 'Weekend dinner at Barbeque Nation'), " +
                    "(1, 'Metro Card Recharge', 500.00, 'Transport', '2026-05-01', 'Monthly metro pass'), " +
                    "(1, 'Cab to Airport', 800.00, 'Transport', '2026-05-04', 'Uber to T3 terminal'), " +
                    "(1, 'Bus Pass', 600.00, 'Transport', '2026-05-06', 'Daily commute bus pass'), " +
                    "(1, 'Electricity Bill', 1800.00, 'Utilities', '2026-05-01', 'Monthly electricity payment'), " +
                    "(1, 'Internet Bill', 999.00, 'Utilities', '2026-05-03', 'ACT Fibernet monthly plan'), " +
                    "(1, 'Mobile Recharge', 599.00, 'Utilities', '2026-05-07', 'Jio prepaid plan'), " +
                    "(1, 'Movie Tickets', 600.00, 'Entertainment', '2026-05-02', 'IMAX show at INOX'), " +
                    "(1, 'Netflix Subscription', 649.00, 'Entertainment', '2026-05-04', 'Monthly Netflix premium'), " +
                    "(1, 'Spotify Premium', 119.00, 'Entertainment', '2026-05-06', 'Music streaming'), " +
                    "(1, 'Gym Membership', 2000.00, 'Health', '2026-05-01', 'Monthly gym fee at Golds Gym'), " +
                    "(1, 'Medicines', 350.00, 'Health', '2026-05-05', 'Cold and flu medication'), " +
                    "(1, 'Doctor Consultation', 1000.00, 'Health', '2026-05-07', 'Annual health checkup'), " +
                    "(1, 'Water Filter Service', 800.00, 'Utilities', '2026-05-08', 'Quarterly RO maintenance'), " +
                    "(1, 'Snacks', 120.00, 'Food', '2026-05-08', 'Chips and soda from local store'), " +
                    "(1, 'Auto Ride', 150.00, 'Transport', '2026-05-08', 'Auto to metro station'), " +
                    "(1, 'Book Purchase', 450.00, 'Other', '2026-05-03', 'Programming book from Amazon'), " +
                    "(1, 'Gift for Birthday', 1500.00, 'Other', '2026-05-06', 'Birthday gift for sister'), " +
                    "(2, 'Breakfast', 200.00, 'Food', '2026-05-01', 'Paratha at local stall'), " +
                    "(2, 'Train Ticket', 350.00, 'Transport', '2026-05-02', 'Local train monthly pass'), " +
                    "(2, 'Phone Bill', 799.00, 'Utilities', '2026-05-03', 'Vi postpaid bill'), " +
                    "(2, 'Concert Tickets', 2500.00, 'Entertainment', '2026-05-04', 'Live music event'), " +
                    "(2, 'Vitamins', 500.00, 'Health', '2026-05-05', 'Monthly supplement pack')");

            System.out.println("[DBUtil] Demo data seeded: 2 users, 25 expenses");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
