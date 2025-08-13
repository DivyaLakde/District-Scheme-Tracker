package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection getConnection() {
        try {
            // Update the URL, user, and password to match your MySQL config
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/scheme_tracker", // your DB name
                    "java_user",     // your DB username
                    "12345"  // your DB password
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ✅ Paste this main() method here to test the connection
    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("✅ Connected to MySQL database!");
        } else {
            System.out.println("❌ Failed to connect to MySQL.");
        }
    }
}

