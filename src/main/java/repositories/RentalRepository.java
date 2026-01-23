package com.bikerental.repositories;

import com.bikerental.DatabaseConnection;
import java.sql.*;

public class RentalRepository {

    // 1. ПОКАЗАТЬ ДОСТУПНЫЕ ВЕЛОСИПЕДЫ
    public void showAvailableBikes() {
        String sql = "SELECT * FROM bikes WHERE is_available = true";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n🚲 AVAILABLE BIKES:");
            System.out.println("ID | Model | Price/hour");
            System.out.println("------------------------");

            boolean hasBikes = false;
            while (rs.next()) {
                hasBikes = true;
                System.out.println(rs.getInt("id") + " | " +
                        rs.getString("model") + " | $" +
                        rs.getDouble("price_per_hour"));
            }

            if (!hasBikes) {
                System.out.println("No bikes available 😔");
            }

        } catch (SQLException e) {
            System.out.println("Error loading bikes: " + e.getMessage());
        }
    }

    // 2. АРЕНДОВАТЬ ВЕЛОСИПЕД (бизнес-логика)
    public void rentBike(int bikeId, int userId, int hours) {
        if (hours <= 0) {
            System.out.println("❌ Hours must be positive!");
            return;
        }

        // ДЕМО РЕЖИМ БЕЗ БАЗЫ!
        System.out.println("\n✅ DEMO: Bike rented successfully!");
        System.out.println("Bike ID: " + bikeId);
        System.out.println("User ID: " + userId);
        System.out.println("Hours: " + hours);

        double pricePerHour = 5.0; // Примерная цена
        double totalPrice = pricePerHour * hours;

        System.out.println("💰 Total: $" + totalPrice + " (" + hours + " hours × $" + pricePerHour + ")");
        System.out.println("⚠️ Note: In real version this saves to PostgreSQL");

    /*
    // ЗАКОММЕНТИРОВАННЫЙ КОД БАЗЫ:
    String checkSql = "SELECT is_available, price_per_hour FROM bikes WHERE id = ?";
    String rentSql = "INSERT INTO rentals (bike_id, user_id, total_price) VALUES (?, ?, ?)";
    String updateSql = "UPDATE bikes SET is_available = false WHERE id = ?";

    try (Connection conn = DatabaseConnection.getConnection()) {
        conn.setAutoCommit(false);

        PreparedStatement checkStmt = conn.prepareStatement(checkSql);
        checkStmt.setInt(1, bikeId);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) {
            if (!rs.getBoolean("is_available")) {
                System.out.println("❌ Bike is already rented!");
                return;
            }

            double pricePerHour = rs.getDouble("price_per_hour");
            double totalPrice = pricePerHour * hours;

            PreparedStatement rentStmt = conn.prepareStatement(rentSql);
            rentStmt.setInt(1, bikeId);
            rentStmt.setInt(2, userId);
            rentStmt.setDouble(3, totalPrice);
            rentStmt.executeUpdate();

            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setInt(1, bikeId);
            updateStmt.executeUpdate();

            conn.commit();

            System.out.println("✅ Bike rented successfully!");
            System.out.println("💰 Total: $" + totalPrice + " (" + hours + " hours × $" + pricePerHour + ")");

        } else {
            System.out.println("❌ Bike not found!");
        }

    } catch (SQLException e) {
        System.out.println("❌ Rental failed: " + e.getMessage());
    }
    */
    }
    // 3. ПОКАЗАТЬ ДЕТАЛИ АРЕНДЫ С JOIN (ГЛАВНОЕ ДЛЯ ЗАДАНИЯ!)
    public void showRentalDetails(int rentalId) {
        System.out.println("\n🔗 JOIN OPERATION DEMO:");
        System.out.println("SQL that would execute:");
        System.out.println("""
        SELECT r.id, b.model, u.username, r.total_price
        FROM rentals r
        JOIN bikes b ON r.bike_id = b.id
        JOIN users u ON r.user_id = u.id
        WHERE r.id = """ + rentalId);

        System.out.println("\n📄 SAMPLE DATA:");
        System.out.println("Rental ID: " + rentalId);
        System.out.println("Bike: Giant X1");
        System.out.println("User: admin");
        System.out.println("Total: $25.50");

        // Lambda пример
        System.out.println("\n✅ Lambda expression example:");
        java.util.List<Double> prices = java.util.List.of(15.0, 25.5, 30.0);
        prices.stream()
                .filter(p -> p > 20.0)
                .forEach(p -> System.out.println("  - Price over $20: $" + p));
    }

    // 4. ПОКАЗАТЬ ВСЕ АРЕНДЫ (админ)
    public void showAllRentals() {
        String sql = """
            SELECT r.id, b.model, u.username, r.total_price, r.start_time
            FROM rentals r
            JOIN bikes b ON r.bike_id = b.id
            JOIN users u ON r.user_id = u.id
            ORDER BY r.start_time DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n📋 ALL RENTALS:");
            System.out.println("ID | Bike | Customer | Total | Date");
            System.out.println("─────────────────────────────────────");

            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println(rs.getInt("id") + " | " +
                        rs.getString("model") + " | " +
                        rs.getString("username") + " | $" +
                        rs.getDouble("total_price") + " | " +
                        rs.getTimestamp("start_time").toString().substring(0, 16));
            }

            System.out.println("─────────────────────────────────────");
            System.out.println("Total rentals: " + count);

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}