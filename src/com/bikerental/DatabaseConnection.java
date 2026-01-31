package com.bikerental;

import java.sql.*;

public class DatabaseConnection {
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/bike_rental",
                    "postgres",
                    "0000"
            );
        } catch (SQLException e) {
            System.out.println(" PostgreSQL Error: " + e.getMessage());
            return null;
        }
    }
}