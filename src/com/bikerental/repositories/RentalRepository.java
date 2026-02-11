package com.bikerental.repositories;

import com.bikerental.DatabaseConnection;
import com.bikerental.models.Rental;
import com.bikerental.models.RentalDetails;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalRepository {

    public void initializeSchema() {
        createUsersTableIfNotExists();
        createRentalsTableIfNotExists();
    }

    private void createUsersTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS public.users (
                id SERIAL PRIMARY KEY,
                first_name VARCHAR(50) NOT NULL,
                last_name VARCHAR(50) NOT NULL,
                phone VARCHAR(20) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Users table: " + e.getMessage());
        }
    }

    private void createRentalsTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS public.rentals (
                id SERIAL PRIMARY KEY,
                bike_id INTEGER NOT NULL,
                user_id INTEGER NOT NULL,
                hours INTEGER NOT NULL CHECK (hours > 0),
                total_price DECIMAL(10,2) NOT NULL,
                status VARCHAR(20) DEFAULT 'ACTIVE',
                rental_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Rentals table: " + e.getMessage());
        }
    }

    public Integer findOrCreateUser(String firstName, String lastName, String phone) {
        Integer userId = findUserByPhone(phone);

        if (userId != null) {
            System.out.println("Found existing customer: ID " + userId);
            return userId;
        }

        return createNewUser(firstName, lastName, phone);
    }

    private Integer findUserByPhone(String phone) {
        String sql = "SELECT id FROM public.users WHERE phone = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            System.out.println("Error finding user: " + e.getMessage());
        }

        return null;
    }

    private Integer createNewUser(String firstName, String lastName, String phone) {
        String sql = "INSERT INTO public.users (first_name, last_name, phone) VALUES (?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, phone);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int newId = rs.getInt("id");
                System.out.println("Created new customer: ID " + newId);
                return newId;
            }

        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
        }

        return null;
    }

    public boolean save(Rental rental) {
        String sql = "INSERT INTO public.rentals (bike_id, user_id, hours, total_price, status) VALUES (?, ?, ?, ?, ?)";

        System.out.println("Saving rental to database...");

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
                        System.out.println("Rental saved! ID: " + rental.getId());
                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error saving rental: " + e.getMessage());
        }

        return false;
    }

    public List<Rental> findAll() {
        String sql = "SELECT * FROM public.rentals ORDER BY rental_date DESC";
        return executeRentalQuery(sql);
    }

    public List<Rental> findRentalsByPeriod(String period) {
        String sql = "";

        switch (period.toUpperCase()) {
            case "TODAY":
                sql = "SELECT * FROM public.rentals WHERE DATE(rental_date) = CURRENT_DATE";
                break;
            case "WEEK":
                sql = "SELECT * FROM public.rentals WHERE rental_date >= CURRENT_DATE - INTERVAL '7 days'";
                break;
            case "MONTH":
                sql = "SELECT * FROM public.rentals WHERE rental_date >= CURRENT_DATE - INTERVAL '30 days'";
                break;
            case "YEAR":
                sql = "SELECT * FROM public.rentals WHERE rental_date >= CURRENT_DATE - INTERVAL '365 days'";
                break;
            default:
                sql = "SELECT * FROM public.rentals";
        }

        return executeRentalQuery(sql + " ORDER BY rental_date DESC");
    }

    public List<Rental> findRentalsByBike(int bikeId) {
        String sql = "SELECT * FROM public.rentals WHERE bike_id = ? ORDER BY rental_date DESC";
        return executeRentalQueryWithParam(sql, bikeId);
    }

    public List<Rental> findRentalsByCustomerName(String firstName, String lastName) {
        String sql = """
        SELECT r.* FROM public.rentals r
        JOIN public.users u ON r.user_id = u.id
        WHERE u.first_name ILIKE ? AND u.last_name ILIKE ?
        ORDER BY r.rental_date DESC
        """;

        return executeCustomerNameQuery(sql, firstName, lastName);
    }

    public List<Rental> findRentalsByCustomerPhone(String phone) {
        String sql = """
        SELECT r.* FROM public.rentals r
        JOIN public.users u ON r.user_id = u.id
        WHERE u.phone = ?
        ORDER BY r.rental_date DESC
        """;

        return executeRentalQueryWithParam(sql, phone);
    }

    private List<Rental> executeCustomerNameQuery(String sql, String firstName, String lastName) {
        List<Rental> rentals = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + firstName + "%");
            pstmt.setString(2, "%" + lastName + "%");

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                rentals.add(mapRentalFromResultSet(rs, conn));
            }
            rs.close();

        } catch (SQLException e) {
            System.out.println("Customer search error: " + e.getMessage());
        }

        return rentals;
    }

    private List<Rental> executeRentalQuery(String sql) {
        List<Rental> rentals = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rentals.add(mapRentalFromResultSet(rs, conn));
            }

        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }

        return rentals;
    }

    private List<Rental> executeRentalQueryWithParam(String sql, Object param) {
        List<Rental> rentals = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, param);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                rentals.add(mapRentalFromResultSet(rs, conn));
            }

            rs.close();

        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }

        return rentals;
    }

    private Rental mapRentalFromResultSet(ResultSet rs, Connection conn) throws SQLException {
        Rental rental = new Rental();

        rental.setId(rs.getInt("id"));
        rental.setBikeId(rs.getInt("bike_id"));
        rental.setUserId(rs.getInt("user_id"));
        rental.setHours(rs.getInt("hours"));
        rental.setTotalPrice(rs.getDouble("total_price"));
        rental.setStatus(rs.getString("status"));

        Timestamp timestamp = rs.getTimestamp("rental_date");
        if (timestamp != null) {
            rental.setRentalDate(timestamp.toLocalDateTime());
        }

        String bikeModel = getBikeModel(conn, rental.getBikeId());
        rental.setBikeModel(bikeModel != null ? bikeModel : "Bike #" + rental.getBikeId());

        String userName = getUserName(conn, rental.getUserId());
        rental.setUserName(userName != null ? userName : "Customer #" + rental.getUserId());

        return rental;
    }

    private String getBikeModel(Connection conn, int bikeId) throws SQLException {
        String sql = "SELECT model FROM public.bikes WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bikeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("model");
            }
        }

        return null;
    }

    private String getUserName(Connection conn, int userId) throws SQLException {
        String sql = "SELECT first_name, last_name FROM public.users WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("first_name") + " " + rs.getString("last_name");
            }
        }

        return null;
    }

    public void showRentalDetails(int rentalId) {
        RentalDetails details = getFullRentalDescription(rentalId);
        if (details == null) {
            System.out.println("Rental ID " + rentalId + " not found");
            try (Connection conn = DatabaseConnection.getConnection()) {
                if (conn != null) {
                    showAvailableRentalIds(conn);
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
            return;
        }
        displayRentalDetails(details);
    }

    public RentalDetails getFullRentalDescription(int rentalId) {
        String sql = """
            SELECT r.id, r.rental_date, r.hours, r.total_price, r.status,
                   b.model as bike_model, b.type as bike_type,
                   c.name as category_name,
                   u.first_name, u.last_name, u.phone
            FROM public.rentals r
            JOIN public.bikes b ON r.bike_id = b.id
            LEFT JOIN public.categories c ON b.category_id = c.id
            JOIN public.users u ON r.user_id = u.id
            WHERE r.id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rentalId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRentalDetails(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return null;
    }

    private RentalDetails mapRentalDetails(ResultSet rs) throws SQLException {
        RentalDetails details = new RentalDetails();
        details.setRentalId(rs.getInt("id"));
        Timestamp timestamp = rs.getTimestamp("rental_date");
        if (timestamp != null) {
            details.setRentalDate(timestamp.toLocalDateTime());
        }
        details.setHours(rs.getInt("hours"));
        details.setTotalPrice(rs.getDouble("total_price"));
        details.setStatus(rs.getString("status"));
        details.setBikeModel(rs.getString("bike_model"));
        details.setBikeType(rs.getString("bike_type"));
        details.setCategoryName(rs.getString("category_name"));
        details.setCustomerName(rs.getString("first_name") + " " + rs.getString("last_name"));
        details.setCustomerPhone(rs.getString("phone"));
        return details;
    }

    private void displayRentalDetails(RentalDetails details) {
        System.out.println("\nRENTAL DETAILS:");
        System.out.println("-".repeat(50));
        System.out.println("Rental ID: " + details.getRentalId());
        System.out.println("Date: " + details.getRentalDate());
        System.out.println("Bike: " + details.getBikeModel());
        System.out.println("Type: " + details.getBikeType());
        System.out.println("Category: " + (details.getCategoryName() == null ? "N/A" : details.getCategoryName()));
        System.out.println("Customer: " + details.getCustomerName());
        System.out.println("Phone: " + details.getCustomerPhone());
        System.out.println("Hours: " + details.getHours());
        System.out.println("Total: $" + details.getTotalPrice());
        System.out.println("Status: " + details.getStatus());
        System.out.println("-".repeat(50));
    }

    private void showAvailableRentalIds(Connection conn) throws SQLException {
        String sql = "SELECT id FROM public.rentals ORDER BY id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<Integer> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }

            if (!ids.isEmpty()) {
                System.out.println("Available Rental IDs: " + ids);
            } else {
                System.out.println("No rentals in database yet");
            }
        }
    }

    public boolean updateBikeStatus(int bikeId, boolean isAvailable) {
        String sql = "UPDATE public.bikes SET is_available = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, isAvailable);
            pstmt.setInt(2, bikeId);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating bike status: " + e.getMessage());
            return false;
        }
    }

    public boolean updateRentalStatus(int rentalId, String status) {
        String sql = "UPDATE public.rentals SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, rentalId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Rental " + rentalId + " status updated to: " + status);
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error updating rental status: " + e.getMessage());
        }

        return false;
    }

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
            System.out.println("Error checking bike: " + e.getMessage());
        }

        return false;
    }

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
            System.out.println("Error checking user: " + e.getMessage());
        }

        return false;
    }

    public boolean deleteRental(int rentalId) {
        String sql = "DELETE FROM public.rentals WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rentalId);
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Rental " + rentalId + " deleted successfully");
                return true;
            } else {
                System.out.println("Rental " + rentalId + " not found");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error deleting rental: " + e.getMessage());
            return false;
        }
    }

    public void showAvailableBikes() {
        String sql = "SELECT * FROM public.bikes WHERE is_available = true ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n" + "═".repeat(50));
            System.out.println("AVAILABLE BIKES:");
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
                System.out.println("No bikes available");
            }

        } catch (SQLException e) {
            System.out.println("Error loading bikes: " + e.getMessage());
        }
    }
}
