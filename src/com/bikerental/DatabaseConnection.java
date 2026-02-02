package com.bikerental;

import java.sql.*;

public class DatabaseConnection {
    public static Connection getConnection() {
        try {

            String url = "jdbc:postgresql://localhost:5432/bike_rental_db";
            String user = "postgres";
            String password = "000000";

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println(" Connected to PostgreSQL database!");
            return conn;

        } catch (SQLException e) {
            System.out.println(" PostgreSQL connection failed!");
            System.out.println(" Error: " + e.getMessage());
            System.out.println(" Check:");
            System.out.println("   1. Is PostgreSQL running?");
            System.out.println("   2. Database: bike_rental_db exists?");
            System.out.println("   3. Password: 0000 is correct?");
            return null;
        }
    }
}