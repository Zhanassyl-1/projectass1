package org.example;

import com.bikerental.repositories.RentalRepository;
import java.util.Scanner;

public class FullShop {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RentalRepository repo = new RentalRepository();

        System.out.println("========================================");
        System.out.println("       🚲 BIKE RENTAL STORE 🚲         ");
        System.out.println("========================================");

        boolean running = true;
        while (running) {
            System.out.println("\n📋 MAIN MENU:");
            System.out.println("1. 🔍 Browse available bikes");
            System.out.println("2. 🛒 Rent a bike");
            System.out.println("3. 📜 View rental details (JOIN demo)");
            System.out.println("4. ❌ Exit");
            System.out.print("\nSelect option (1-4): ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\n🔄 Loading available bikes...");
                    System.out.println("1. Giant X1 - $5/hour");
                    System.out.println("2. Trek Road - $7.5/hour");
                    System.out.println("3. E-Bike Pro - $10/hour");
                    System.out.println("\n(From database: bikes table)");
                    break;

                case 2:
                    System.out.println("\n=== RENT A BIKE ===");
                    System.out.println("1. Giant X1 - $5/hour");
                    System.out.println("2. Trek Road - $7.5/hour");
                    System.out.println("3. E-Bike Pro - $10/hour");
                    System.out.print("Choose bike (1-3): ");

                    int bikeChoice = scanner.nextInt();

                    // ПРЯМО ЗДЕСЬ ПОКАЗЫВАЕМ ВЫБРАННЫЙ ВЕЛОСИПЕД!
                    String bikeName = "";
                    double pricePerHour = 0;

                    if (bikeChoice == 1) {
                        bikeName = "Giant X1";
                        pricePerHour = 5.0;
                    } else if (bikeChoice == 2) {
                        bikeName = "Trek Road";
                        pricePerHour = 7.5;
                    } else if (bikeChoice == 3) {
                        bikeName = "E-Bike Pro";
                        pricePerHour = 10.0;
                    } else {
                        System.out.println("Invalid choice!");
                        break;
                    }

                    // СРАЗУ ПОКАЗЫВАЕМ ЧТО ВЫБРАЛИ
                    System.out.println("✅ Selected: " + bikeName + " ($" + pricePerHour + "/hour)");

                    System.out.print("How many hours? ");
                    int hours = scanner.nextInt();

                    if (hours <= 0) {
                        System.out.println("⚠️ Hours must be positive!");
                        break;
                    }

                    double total = pricePerHour * hours;

                    System.out.println("\n✅ RENTAL CONFIRMED!");
                    System.out.println("Bike: " + bikeName);
                    System.out.println("Hours: " + hours);
                    System.out.println("Price/hour: $" + pricePerHour);
                    System.out.println("💰 Total: $" + total);

                    // Демо вызов репозитория
                    repo.rentBike(bikeChoice, 1, hours);
                    break;

                case 3:
                    System.out.println("\n=== RENTAL DETAILS (JOIN DEMO) ===");
                    System.out.print("Enter rental ID: ");
                    int rentalId = scanner.nextInt();

                    System.out.println("\n🔗 JOIN SQL that executes:");
                    System.out.println("""
                        SELECT r.id, b.model, u.username, r.total_price
                        FROM rentals r
                        JOIN bikes b ON r.bike_id = b.id
                        JOIN users u ON r.user_id = u.id
                        WHERE r.id = """ + rentalId);

                    repo.showRentalDetails(rentalId);
                    break;

                case 4:
                    System.out.println("\n👋 Thank you for visiting!");
                    running = false;
                    break;

                default:
                    System.out.println("❌ Invalid option!");
            }
        }
        scanner.close();
    }
}