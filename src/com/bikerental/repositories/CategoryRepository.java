package com.bikerental.repositories;

import com.bikerental.DatabaseConnection;
import com.bikerental.models.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    public void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS public.categories (
                id SERIAL PRIMARY KEY,
                name VARCHAR(60) NOT NULL UNIQUE
            )
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Categories table: " + e.getMessage());
        }
    }

    public void seedDefaultsIfEmpty() {
        String checkSql = "SELECT COUNT(*) FROM public.categories";
        String insertSql = "INSERT INTO public.categories (name) VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                for (String name : List.of("Road", "Mountain", "City")) {
                    pstmt.setString(1, name);
                    pstmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            System.out.println("Category seeding: " + e.getMessage());
        }
    }

    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM public.categories ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(new Category(rs.getInt("id"), rs.getString("name")));
            }

        } catch (SQLException e) {
            System.out.println("Error loading categories: " + e.getMessage());
        }

        return categories;
    }

    public boolean create(String name) {
        String sql = "INSERT INTO public.categories (name) VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error creating category: " + e.getMessage());
            return false;
        }
    }
}
