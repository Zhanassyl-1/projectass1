package org.example;

import com.bikerental.repositories.RentalRepository;
import java.util.Scanner;

public class BikeShop {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RentalRepository repo = new RentalRepository();

        System.out.println("🚲 WELCOME TO BIKE RENTAL SHOP!");

        while (true) {
            System.out.println("\n=== MENU ===");
            System.out.println("1. View available bikes");
            System.out.println("2. Rent a bike");
            System.out.println("3. View my rentals");
            System.out.println("4. Admin: View all rentals (JOIN demo)");
            System.out.println("5. Exit");
            System.out.print("Choice: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\nAvailable bikes:");
                    System.out.println("1. Giant X1 - $5/hour");
                    System.out.println("2. Trek Road - $7.5/hour");
                    System.out.println("3. E-Bike Pro - $10/hour");
                    break;

                case 2:
                    System.out.println("\nChoose bike:");
                    System.out.println("1. Giant X1 - $5/hour");
                    System.out.println("2. Trek Road - $7.5/hour");
                    System.out.println("3. E-Bike Pro - $10/hour");
                    System.out.print("Your choice (1-3): ");
                    int bikeChoice = scanner.nextInt();

                    String[] bikeNames = {"", "Giant X1", "Trek Road", "E-Bike Pro"};
                    double[] bikePrices = {0, 5.0, 7.5, 10.0};

                    if (bikeChoice < 1 || bikeChoice > 3) {
                        System.out.println("Invalid choice!");
                        break;
                    }

                    System.out.print("Hours to rent: ");
                    int hours = scanner.nextInt();

// Автоматически user ID = 1 (демо)
                    int userId = 1;
                    String bikeName = bikeNames[bikeChoice];
                    double pricePerHour = bikePrices[bikeChoice];

                    System.out.println("\n✅ Renting: " + bikeName);
                    System.out.println("User: Demo User (ID: 1)");
                    System.out.println("Hours: " + hours);
                    System.out.println("Price/hour: $" + pricePerHour);

                    double total = pricePerHour * hours;
                    System.out.println("💰 Total: $" + total);
                    System.out.print("Hours to rent: ");



                    System.out.println("✅ Bike rented! Total: $" + (hours * 5));
                    break;

                case 3:
                    System.out.println("\nYour rentals:");
                    System.out.println("1. Giant X1 - 3 hours - $15");
                    System.out.println("2. E-Bike Pro - 2 hours - $20");
                    break;

                case 4:
                    System.out.print("\nEnter rental ID to view details: ");
                    int rentalId = scanner.nextInt();
                    repo.showRentalDetails(rentalId);
                    break;

                case 5:
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}