package com.bikerental.repositories;

import com.bikerental.DatabaseConnection;
import com.bikerental.models.Bike;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class BikeRepository {

    public void initializeSchema() {
        createBikesTableIfNotExists();
        addCategoryColumnIfMissing();
        addCategoryForeignKey();
    }

    private void createBikesTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS public.bikes (
                id SERIAL PRIMARY KEY,
                model VARCHAR(100) NOT NULL,
                type VARCHAR(50),
                price_per_hour DECIMAL(10,2) NOT NULL,
                is_available BOOLEAN DEFAULT true,
                category_id INTEGER
            )
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Bikes table: " + e.getMessage());
        }
    }

    private void addCategoryColumnIfMissing() {
        String sql = "ALTER TABLE public.bikes ADD COLUMN IF NOT EXISTS category_id INTEGER";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Bikes category column: " + e.getMessage());
        }
    }

    private void addCategoryForeignKey() {
        String sql = "ALTER TABLE public.bikes " +
                "ADD CONSTRAINT bikes_category_fk FOREIGN KEY (category_id) " +
                "REFERENCES public.categories(id)";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            String message = e.getMessage();
            if (message == null || !message.contains("already exists")) {
                System.out.println("Bikes category FK: " + e.getMessage());
            }
        }
    }

    public List<Bike> findAllAvailable() {
        List<Bike> bikes = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println(" Нет подключения к БД!");
                return bikes;
            }

            String query = """
                SELECT b.id, b.model, b.type, b.price_per_hour, b.is_available, b.category_id,
                       c.name AS category_name
                FROM public.bikes b
                LEFT JOIN public.categories c ON b.category_id = c.id
                WHERE b.is_available = true
                ORDER BY b.id
                """;
            System.out.println("📊 SQL: " + query);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            int count = 0;
            while (rs.next()) {
                Bike bike = new Bike();
                bike.setId(rs.getInt("id"));
                bike.setModel(rs.getString("model"));
                bike.setType(rs.getString("type"));
                bike.setPricePerHour(rs.getDouble("price_per_hour"));
                bike.setAvailable(rs.getBoolean("is_available"));
                bike.setCategoryId((Integer) rs.getObject("category_id"));
                bike.setCategoryName(rs.getString("category_name"));

                bikes.add(bike);
                count++;
            }

            rs.close();
            stmt.close();
            conn.close();

            System.out.println(" Загружено велосипедов из БД: " + count);

        } catch (Exception e) {
            System.out.println(" Ошибка загрузки велосипедов: " + e.getMessage());
        }

        return bikes;
    }

    public Bike findById(int id) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println(" Нет подключения к БД!");
                return null;
            }

            PreparedStatement pstmt = conn.prepareStatement("""
                SELECT b.id, b.model, b.type, b.price_per_hour, b.is_available, b.category_id,
                       c.name AS category_name
                FROM public.bikes b
                LEFT JOIN public.categories c ON b.category_id = c.id
                WHERE b.id = ?
                """);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Bike bike = new Bike();
                bike.setId(rs.getInt("id"));
                bike.setModel(rs.getString("model"));
                bike.setType(rs.getString("type"));
                bike.setPricePerHour(rs.getDouble("price_per_hour"));
                bike.setAvailable(rs.getBoolean("is_available"));
                bike.setCategoryId((Integer) rs.getObject("category_id"));
                bike.setCategoryName(rs.getString("category_name"));

                rs.close();
                pstmt.close();
                conn.close();
                return bike;
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            System.out.println(" Ошибка поиска велосипеда: " + e.getMessage());
        }
        return null;
    }

    public boolean updateStatus(int bikeId, String status) {
        boolean isAvailable = status.equals("AVAILABLE");

        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println(" Нет подключения к БД!");
                return false;
            }

            PreparedStatement pstmt = conn.prepareStatement("UPDATE public.bikes SET is_available = ? WHERE id = ?");
            pstmt.setBoolean(1, isAvailable);
            pstmt.setInt(2, bikeId);

            int rows = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            System.out.println(" Обновлен статус велосипеда ID=" + bikeId + ", is_available=" + isAvailable);
            return rows > 0;

        } catch (Exception e) {
            System.out.println(" Ошибка обновления статуса: " + e.getMessage());
            return false;
        }
    }
}
