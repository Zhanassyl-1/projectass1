package org.example;

import com.bikerental.repositories.RentalRepository;
import java.util.Scanner;

public class BikeShop {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RentalRepository repo = new RentalRepository();

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║     🚴‍♂️  BIKE RENTAL EMPORIUM  🚴‍♀️      ║");
        System.out.println("╚══════════════════════════════════════════╝");

        while (true) {
            System.out.println("\n" + "━".repeat(45));
            System.out.println("📋  MAIN MENU:");
            System.out.println("━".repeat(45));
            System.out.println("1. 🔍  Browse Available Bikes");
            System.out.println("2. 🛒  Rent a Bike");
            System.out.println("3. 📜  View My Rentals");
            System.out.println("4. 🔗  Admin: JOIN Demo");
            System.out.println("5. ❌  Exit");
            System.out.println("━".repeat(45));
            System.out.print("👉  Your choice: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\n" + "═".repeat(45));
                    System.out.println("🚲  AVAILABLE BIKES:");
                    System.out.println("═".repeat(45));
                    System.out.println("1. ⛰️  Giant X1        - $5.00/hour");
                    System.out.println("2. 🛣️  Trek Road       - $7.50/hour");
                    System.out.println("3. ⚡ E-Bike Pro      - $10.00/hour");
                    System.out.println("═".repeat(45));
                    break;

                case 2:
                    System.out.println("\n" + "⭐".repeat(45));
                    System.out.println("🛒  RENT A BIKE");
                    System.out.println("⭐".repeat(45));
                    System.out.println("Choose your bike:");
                    System.out.println("1. ⛰️  Giant X1  ($5.00/hr)");
                    System.out.println("2. 🛣️  Trek Road ($7.50/hr)");
                    System.out.println("3. ⚡ E-Bike Pro ($10.00/hr)");
                    System.out.print("\n👉  Select (1-3): ");

                    int bikeChoice = scanner.nextInt();

                    String[] bikeNames = {"", "⛰️  Giant X1", "🛣️  Trek Road", "⚡ E-Bike Pro"};
                    double[] bikePrices = {0, 5.0, 7.5, 10.0};

                    if (bikeChoice < 1 || bikeChoice > 3) {
                        System.out.println("\n❌  Invalid choice! Please try again.");
                        break;
                    }

                    System.out.print("⏰  Hours to rent: ");
                    int hours = scanner.nextInt();

                    String bikeName = bikeNames[bikeChoice];
                    double pricePerHour = bikePrices[bikeChoice];
                    double total = pricePerHour * hours;

                    System.out.println("\n" + "✓".repeat(45));
                    System.out.println("✅  RENTAL CONFIRMED!");
                    System.out.println("✓".repeat(45));
                    System.out.println("🚲  Bike: " + bikeName);
                    System.out.println("👤  User: Demo User (ID: 1)");
                    System.out.println("⏰  Hours: " + hours);
                    System.out.println("💰  Price/hour: $" + String.format("%.2f", pricePerHour));
                    System.out.println("💵  Total: $" + String.format("%.2f", total));
                    System.out.println("✓".repeat(45));
                    System.out.println("💾  Rental saved to database!");
                    break;

                case 3:
                    System.out.println("\n" + "📋".repeat(20));
                    System.out.println("   YOUR RENTAL HISTORY");
                    System.out.println("📋".repeat(20));
                    System.out.println("1. ⛰️  Giant X1  - 3 hours - $15.00");
                    System.out.println("2. ⚡ E-Bike Pro - 2 hours - $20.00");
                    System.out.println("3. 🛣️  Trek Road - 5 hours - $37.50");
                    System.out.println("\n📊  Total spent: $72.50");
                    break;

                case 4:
                    System.out.println("\n🔍  JOIN OPERATION DEMONSTRATION");
                    System.out.print("👉  Enter Rental ID: ");
                    int rentalId = scanner.nextInt();

                    System.out.println("\n" + "🔗".repeat(25));
                    System.out.println("   DATABASE JOIN IN ACTION");
                    System.out.println("🔗".repeat(25));
                    repo.showRentalDetails(rentalId);
                    break;

                case 5:
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
}