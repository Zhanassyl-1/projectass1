package com.bikerental;

import java.sql.*;

public class DatabaseConnection {
    public static Connection getConnection() {
        try {
            String url = "jdbc:postgresql://localhost:5432/bike_rental_db";
            String user = "postgres";
            String password = "0000";

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✓ Connected to PostgreSQL database!");
            return conn;

        } catch (SQLException e) {
            System.out.println("✗ PostgreSQL connection failed!");
            System.out.println("Error: " + e.getMessage());
            System.out.println("Check parameters:");
            System.out.println("   URL: jdbc:postgresql://localhost:5432/bike_rental_db");
            System.out.println("   User: postgres");
            System.out.println("   Password: 0000 (change if different)");
            return null;
        }
    }
}