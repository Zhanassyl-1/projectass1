package com.bikerental;

import java.sql.*;

public class DatabaseConnection {

    public static Connection getConnection() {
        try {
            // Пароль ПОМЕНЯЙ НА СВОЙ!
            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/bike_rental_db",
                    "postgres",
                    "123" // ← ТВОЙ ПАРОЛЬ ЗДЕСЬ!
            );
        } catch (SQLException e) {
            System.out.println("⚠️ DB Error: " + e.getMessage());
            return null;
        }
    }
}