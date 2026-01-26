package com.bikerental.repositories;

import com.bikerental.DatabaseConnection;
import com.bikerental.models.Rental;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalRepository {

    // 1. ПОКАЗАТЬ ДОСТУПНЫЕ ВЕЛОСИПЕДЫ
    public void showAvailableBikes() {
        String sql = "SELECT * FROM public.bikes WHERE is_available = true";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n" + "═".repeat(50));
            System.out.println("🚲 AVAILABLE BIKES FROM DATABASE:");
            System.out.println("═".repeat(50));
            System.out.printf("%-4s %-25s %-15s%n", "ID", "MODEL", "PRICE/HOUR");
            System.out.println("-".repeat(45));

            boolean hasBikes = false;
            while (rs.next()) {
                hasBikes = true;
                System.out.printf("%-4d %-25s $%-14.2f%n",
                        rs.getInt("id"),
                        rs.getString("model"),
                        rs.getDouble("price_per_hour"));
            }

            if (!hasBikes) {
                System.out.println("📭 No bikes available");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error loading bikes: " + e.getMessage());
        }
    }

    // 2. СОХРАНИТЬ АРЕНДУ В БАЗУ
    public boolean save(Rental rental) {
        String sql = "INSERT INTO public.rentals (bike_id, user_id, hours, total_price, status) VALUES (?, ?, ?, ?, ?)";

        System.out.println("💾 Saving rental to database...");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, rental.getBikeId());
            pstmt.setInt(2, rental.getUserId());
            pstmt.setInt(3, rental.getHours());
            pstmt.setDouble(4, rental.getTotalPrice());
            pstmt.setString(5, rental.getStatus());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        rental.setId(rs.getInt(1));
                        System.out.println("✅ Rental saved to database! ID: " + rental.getId());
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error saving rental: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // 3. ПОЛУЧИТЬ ВСЕ АРЕНДЫ
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.out.println("❌ No database connection!");
                return rentals;
            }

            String sql = "SELECT * FROM public.rentals ORDER BY id DESC";
            System.out.println("📊 SQL запрос: " + sql);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            int count = 0;
            while (rs.next()) {
                Rental rental = new Rental();

                rental.setId(rs.getInt("id"));
                rental.setBikeId(rs.getInt("bike_id"));
                rental.setUserId(rs.getInt("user_id"));
                rental.setHours(rs.getInt("hours"));
                rental.setTotalPrice(rs.getDouble("total_price"));
                rental.setStatus(rs.getString("status"));

                // Получаем название велосипеда
                try {
                    String bikeSql = "SELECT model FROM public.bikes WHERE id = ?";
                    PreparedStatement bikeStmt = conn.prepareStatement(bikeSql);
                    bikeStmt.setInt(1, rental.getBikeId());
                    ResultSet bikeRs = bikeStmt.executeQuery();

                    if (bikeRs.next()) {
                        rental.setBikeModel(bikeRs.getString("model"));
                    } else {
                        rental.setBikeModel("Bike #" + rental.getBikeId());
                    }

                    bikeRs.close();
                    bikeStmt.close();

                } catch (SQLException e) {
                    rental.setBikeModel("Unknown");
                }

                rentals.add(rental);
                count++;

                System.out.println("📝 Найден: ID=" + rental.getId() +
                        ", Bike=" + rental.getBikeId() +
                        ", User=" + rental.getUserId() +
                        ", Hours=" + rental.getHours() +
                        ", Total=$" + rental.getTotalPrice());
            }

            rs.close();
            stmt.close();
            conn.close();

            System.out.println("✅ Загружено аренд из БД: " + count);

        } catch (SQLException e) {
            System.out.println("❌ Ошибка загрузки аренд: " + e.getMessage());
            e.printStackTrace();
        }

        return rentals;
    }

    // 4. ПОКАЗАТЬ ДЕТАЛИ АРЕНДЫ С JOIN
    public void showRentalDetails(int rentalId) {
        System.out.println("\n" + "🔗".repeat(25));
        System.out.println("   DATABASE JOIN DEMONSTRATION");
        System.out.println("🔗".repeat(25));

        String sql = """
        SELECT r.id, r.created_at, r.hours, r.total_price, r.status,
               b.model as bike_model, u.name as user_name
        FROM public.rentals r
        JOIN public.bikes b ON r.bike_id = b.id
        JOIN public.users u ON r.user_id = u.id
        WHERE r.id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rentalId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("\n📄 RENTAL DETAILS FROM DATABASE:");
                System.out.println("-".repeat(50));
                System.out.println("📋 Rental ID: " + rs.getInt("id"));
                System.out.println("📅 Date: " + rs.getTimestamp("created_at"));
                System.out.println("🚲 Bike: " + rs.getString("bike_model"));
                System.out.println("👤 Customer: " + rs.getString("user_name"));
                System.out.println("⏰ Hours: " + rs.getInt("hours"));
                System.out.println("💰 Total: $" + rs.getDouble("total_price"));
                System.out.println("📊 Status: " + rs.getString("status"));

            } else {
                System.out.println("❌ Rental ID " + rentalId + " not found");

                // Покажем доступные ID
                String allSql = "SELECT id FROM public.rentals ORDER BY id";
                Statement stmt = conn.createStatement();
                ResultSet allRs = stmt.executeQuery(allSql);

                List<Integer> ids = new ArrayList<>();
                while (allRs.next()) {
                    ids.add(allRs.getInt("id"));
                }
                allRs.close();
                stmt.close();

                if (!ids.isEmpty()) {
                    System.out.println("💡 Available rental IDs: " + ids);
                } else {
                    System.out.println("💡 No rentals in database yet.");
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());

            // Если таблица users не существует
            if (e.getMessage().contains("отношение \"users\" не существует")) {
                System.out.println("⚠️ Table 'users' doesn't exist.");

                // Альтернативный запрос без JOIN с users
                String simpleSql = "SELECT * FROM public.rentals WHERE id = ?";
                try (Connection conn2 = DatabaseConnection.getConnection();
                     PreparedStatement pstmt2 = conn2.prepareStatement(simpleSql)) {

                    pstmt2.setInt(1, rentalId);
                    ResultSet rs2 = pstmt2.executeQuery();

                    if (rs2.next()) {
                        System.out.println("\n📊 Rental exists (no users table):");
                        System.out.println("   ID: " + rs2.getInt("id"));
                        System.out.println("   Bike ID: " + rs2.getInt("bike_id"));
                        System.out.println("   User ID: " + rs2.getInt("user_id"));
                        System.out.println("   Hours: " + rs2.getInt("hours"));
                        System.out.println("   Total: $" + rs2.getDouble("total_price"));
                        System.out.println("   Status: " + rs2.getString("status"));
                    }
                } catch (SQLException e2) {
                    System.out.println("Error checking rental: " + e2.getMessage());
                }
            }
        }
    }

    // 5. ОБНОВИТЬ СТАТУС ВЕЛОСИПЕДА
    public boolean updateBikeStatus(int bikeId, boolean isAvailable) {
        String sql = "UPDATE public.bikes SET is_available = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, isAvailable);
            pstmt.setInt(2, bikeId);

            int rows = pstmt.executeUpdate();
            System.out.println("🔄 Bike ID " + bikeId + " status updated to: " + (isAvailable ? "AVAILABLE" : "RENTED"));
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error updating bike status: " + e.getMessage());
            return false;
        }
    }

    // 6. СОЗДАТЬ ТАБЛИЦУ RENTALS ЕСЛИ НЕ СУЩЕСТВУЕТ
    public void createRentalsTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS public.rentals (
                id SERIAL PRIMARY KEY,
                bike_id INTEGER NOT NULL,
                user_id INTEGER NOT NULL,
                hours INTEGER NOT NULL CHECK (hours > 0),
                total_price DECIMAL(10,2) NOT NULL,
                status VARCHAR(20) DEFAULT 'ACTIVE',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("✅ Table 'rentals' created or already exists!");

        } catch (SQLException e) {
            System.out.println("❌ Error creating rentals table: " + e.getMessage());
        }
    }

    // 7. СОЗДАТЬ ТАБЛИЦУ USERS ЕСЛИ НЕ СУЩЕСТВУЕТ
    public void createUsersTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS public.users (
                id SERIAL PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(100) UNIQUE NOT NULL
            )
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

            // Добавляем тестового пользователя
            String insertSql = "INSERT INTO public.users (name, email) VALUES ('Demo User', 'demo@example.com') ON CONFLICT DO NOTHING";
            stmt.execute(insertSql);

            System.out.println("✅ Table 'users' and demo data created!");

        } catch (SQLException e) {
            System.out.println("❌ Error creating users table: " + e.getMessage());
        }
    }

    // 8. ПРОВЕРИТЬ СУЩЕСТВОВАНИЕ ПОЛЬЗОВАТЕЛЯ
    public boolean userExists(int userId) {
        String sql = "SELECT COUNT(*) FROM public.users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error checking user: " + e.getMessage());
        }
        return false;
    }

    // 9. ПРОВЕРИТЬ СУЩЕСТВОВАНИЕ ВЕЛОСИПЕДА
    public boolean bikeExists(int bikeId) {
        String sql = "SELECT COUNT(*) FROM public.bikes WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bikeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error checking bike: " + e.getMessage());
        }
        return false;
    }

    // 10. УДАЛИТЬ АРЕНДУ ПО ID
    public boolean deleteRental(int rentalId) {
        String sql = "DELETE FROM public.rentals WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rentalId);
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Rental ID " + rentalId + " deleted successfully!");
                return true;
            } else {
                System.out.println("❌ Rental ID " + rentalId + " not found!");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error deleting rental: " + e.getMessage());
            return false;
        }
    }
}