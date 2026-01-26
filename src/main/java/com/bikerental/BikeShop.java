package com.bikerental;

import com.bikerental.repositories.BikeRepository;
import com.bikerental.repositories.RentalRepository;
import com.bikerental.models.Bike;
import com.bikerental.models.Rental;
import java.util.Scanner;
import java.util.List;

public class BikeShop {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BikeRepository bikeRepo = new BikeRepository();
        RentalRepository rentalRepo = new RentalRepository();

        // ИНИЦИАЛИЗАЦИЯ БАЗЫ ДАННЫХ ПРИ ЗАПУСКЕ
        System.out.println("🔄 Initializing database...");
        rentalRepo.createRentalsTableIfNotExists();
        rentalRepo.createUsersTableIfNotExists();

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║     🚴‍♂️  BIKE RENTAL EMPORIUM  🚴‍♀️      ║");
        System.out.println("╚══════════════════════════════════════════╝");

        while (true) {
            System.out.println("\n" + "━".repeat(45));
            System.out.println("📋  MAIN MENU:");
            System.out.println("━".repeat(45));
            System.out.println("1. 🔍  Browse Available Bikes (FROM DATABASE)");
            System.out.println("2. 🛒  Rent a Bike (REAL DATABASE)");
            System.out.println("3. 📜  View All Rentals (FROM DATABASE)");
            System.out.println("4. 🔗  Admin: JOIN Demo");
            System.out.println("5. 🛠️   Initialize Database");
            System.out.println("6. ❌  Exit");
            System.out.println("━".repeat(45));
            System.out.print("👉  Your choice: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    displayAllBikes(bikeRepo);
                    break;

                case 2:
                    processBikeRental(scanner, bikeRepo, rentalRepo);
                    break;

                case 3:
                    displayRentalHistory(rentalRepo);
                    break;

                case 4:
                    System.out.println("\n🔍  JOIN OPERATION DEMONSTRATION");
                    System.out.print("👉  Enter Rental ID: ");
                    int rentalId = scanner.nextInt();

                    System.out.println("\n" + "🔗".repeat(25));
                    System.out.println("   DATABASE JOIN IN ACTION");
                    System.out.println("🔗".repeat(25));
                    rentalRepo.showRentalDetails(rentalId);
                    break;

                case 5:
                    System.out.println("\n🛠️  INITIALIZING DATABASE...");
                    rentalRepo.createRentalsTableIfNotExists();
                    rentalRepo.createUsersTableIfNotExists();
                    System.out.println("✅ Database initialized!");
                    break;

                case 6:
                    System.out.println("\n" + "❤️".repeat(20));
                    System.out.println("   Thank you for visiting!");
                    System.out.println("   Come back soon! 👋");
                    System.out.println("❤️".repeat(20));
                    scanner.close();
                    return;

                default:
                    System.out.println("\n⚠️  Invalid choice! Please try again.");
            }
        }
    }

    private static void displayAllBikes(BikeRepository bikeRepo) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("🚲  AVAILABLE BIKES FROM DATABASE");
        System.out.println("═".repeat(60));

        try {
            List<Bike> bikes = bikeRepo.findAllAvailable();

            if (bikes.isEmpty()) {
                System.out.println("📭  No bikes available in the database.");
            } else {
                System.out.printf("%-4s %-30s %-15s %-10s%n", "ID", "MODEL", "PRICE/HOUR", "STATUS");
                System.out.println("-".repeat(65));

                int index = 1;
                for (Bike bike : bikes) {
                    String emoji = "⚡";
                    if (bike.getModel().toLowerCase().contains("mountain")) emoji = "⛰️";
                    else if (bike.getModel().toLowerCase().contains("road")) emoji = "🛣️";
                    else if (bike.getModel().toLowerCase().contains("hybrid")) emoji = "🚲";

                    System.out.printf("%-4d %-30s $%-14.2f %s%n",
                            bike.getId(),
                            emoji + "  " + bike.getModel(),
                            bike.getPricePerHour(),
                            bike.isAvailable() ? "✅" : "⛔");
                }

                System.out.println("\n📊 TOTAL: " + bikes.size() + " bikes available");
            }

        } catch (Exception e) {
            System.out.println("❌ Error loading bikes: " + e.getMessage());
        }
    }

    private static void processBikeRental(Scanner scanner, BikeRepository bikeRepo, RentalRepository rentalRepo) {
        System.out.println("\n" + "⭐".repeat(45));
        System.out.println("🛒  RENT A BIKE FROM DATABASE");
        System.out.println("⭐".repeat(45));

        // Сначала показываем доступные велосипеды
        System.out.println("\n📍 STEP 1: SELECT BICYCLE FROM DATABASE");

        List<Bike> availableBikes = bikeRepo.findAllAvailable();

        if (availableBikes.isEmpty()) {
            System.out.println("❌ No bikes available for rent in the database.");
            return;
        }

        System.out.printf("%-4s %-30s %-15s%n", "ID", "MODEL", "PRICE/HOUR");
        System.out.println("-".repeat(55));

        for (Bike bike : availableBikes) {
            String emoji = "⚡";
            if (bike.getModel().toLowerCase().contains("mountain")) emoji = "⛰️";
            else if (bike.getModel().toLowerCase().contains("road")) emoji = "🛣️";

            System.out.printf("%-4d %-30s $%-14.2f%n",
                    bike.getId(),
                    emoji + "  " + bike.getModel(),
                    bike.getPricePerHour());
        }

        System.out.print("\n👉  Enter Bike ID from the list above: ");
        int bikeId = scanner.nextInt();

        // Проверяем, существует ли такой велосипед
        Bike selectedBike = bikeRepo.findById(bikeId);

        if (selectedBike == null) {
            System.out.println("❌ Bike with ID " + bikeId + " not found in database!");
            return;
        }

        if (!selectedBike.isAvailable()) {
            System.out.println("❌ This bike is already rented!");
            return;
        }

        // Ввод данных пользователя
        System.out.print("👤  Enter User ID: ");
        int userId = scanner.nextInt();

        System.out.print("⏰  Hours to rent: ");
        int hours = scanner.nextInt();

        if (hours < 1 || hours > 24) {
            System.out.println("❌ Hours must be between 1 and 24!");
            return;
        }

        // Расчет стоимости
        double pricePerHour = selectedBike.getPricePerHour();
        double total = pricePerHour * hours;

        // Показываем детали
        System.out.println("\n" + "✓".repeat(45));
        System.out.println("✅  RENTAL CONFIRMED!");
        System.out.println("✓".repeat(45));

        String emoji = "⚡";
        if (selectedBike.getModel().toLowerCase().contains("mountain")) emoji = "⛰️";
        else if (selectedBike.getModel().toLowerCase().contains("road")) emoji = "🛣️";

        System.out.println("🚲  Bike: " + emoji + "  " + selectedBike.getModel());
        System.out.println("👤  User ID: " + userId);
        System.out.println("⏰  Hours: " + hours);
        System.out.println("💰  Price/hour: $" + String.format("%.2f", pricePerHour));
        System.out.println("💵  Total: $" + String.format("%.2f", total));
        System.out.println("✓".repeat(45));

        // Подтверждение
        System.out.print("\n✅  Confirm rental? (yes/no): ");
        scanner.nextLine(); // очистка буфера
        String confirm = scanner.nextLine().toLowerCase();

        if (confirm.equals("yes") || confirm.equals("y")) {
            try {
                // Сохраняем аренду в базу
                Rental rental = new Rental();
                rental.setBikeId(bikeId);
                rental.setUserId(userId);
                rental.setHours(hours);
                rental.setTotalPrice(total);
                rental.setStatus("ACTIVE");

                // ДЕБАГ: покажем что установили
                System.out.println("🔍 DEBUG созданного rental:");
                System.out.println("   Bike ID: " + rental.getBikeId());
                System.out.println("   User ID: " + rental.getUserId());
                System.out.println("   Hours: " + rental.getHours());
                System.out.println("   Total: $" + rental.getTotalPrice());

                boolean saved = rentalRepo.save(rental);

                if (saved) {
                    // Обновляем статус велосипеда
                    boolean updated = bikeRepo.updateStatus(bikeId, "RENTED");

                    if (updated) {
                        System.out.println("💾  Rental saved to database!");
                        System.out.println("🔄  Bike status updated to RENTED");
                    } else {
                        System.out.println("⚠️  Rental saved but bike status not updated!");
                    }
                } else {
                    System.out.println("❌  Failed to save rental!");
                }

            } catch (Exception e) {
                System.out.println("❌  Error saving rental: " + e.getMessage());
            }
        } else {
            System.out.println("❌  Rental cancelled.");
        }
    }

    private static void displayRentalHistory(RentalRepository rentalRepo) {
        System.out.println("\n" + "📋".repeat(20));
        System.out.println("   ALL RENTALS FROM DATABASE");
        System.out.println("📋".repeat(20));

        try {
            List<Rental> rentals = rentalRepo.findAll();

            if (rentals.isEmpty()) {
                System.out.println("📭  No rentals found in database.");
            } else {
                System.out.println("\n" + "-".repeat(70));
                System.out.printf("%-10s %-20s %-15s %-12s %-10s%n",
                        "RENTAL ID", "BIKE", "USER ID", "HOURS", "TOTAL");
                System.out.println("-".repeat(70));

                double totalSpent = 0;
                for (Rental rental : rentals) {
                    System.out.printf("RNT-%-6d %-20s %-15d %-12d $%-9.2f%n",
                            rental.getId(),
                            rental.getBikeModel() != null ? rental.getBikeModel() : "Unknown",
                            rental.getUserId(),
                            rental.getHours(),
                            rental.getTotalPrice());
                    totalSpent += rental.getTotalPrice();
                }

                System.out.println("\n📊  Total spent: $" + String.format("%.2f", totalSpent));
                System.out.println("📊  Total rentals: " + rentals.size());
            }

        } catch (Exception e) {
            System.out.println("❌ Error loading rentals: " + e.getMessage());
        }
    }
}