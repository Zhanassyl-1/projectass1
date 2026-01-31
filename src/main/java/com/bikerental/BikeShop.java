package com.bikerental;

import com.bikerental.repositories.BikeRepository;
import com.bikerental.repositories.RentalRepository;
import com.bikerental.models.Bike;
import com.bikerental.models.Rental;
import java.util.Scanner;
import java.util.List;
import java.util.InputMismatchException;

public class BikeShop {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BikeRepository bikeRepo = new BikeRepository();
        RentalRepository rentalRepo = new RentalRepository();

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║          BIKE RENTAL EMPORIUM            ║");
        System.out.println("╚══════════════════════════════════════════╝");

        System.out.println("\nChecking database setup...");
        rentalRepo.createTablesIfNotExist();

        while (true) {
            printMainMenu();
            int choice = getMenuChoice(scanner);

            switch (choice) {
                case 1:
                    displayAllBikes(bikeRepo);
                    break;
                case 2:
                    rentBike(scanner, bikeRepo, rentalRepo);
                    break;
                case 3:
                    showRentalHistoryMenu(scanner, rentalRepo);
                    break;
                case 4:
                    initializeDatabase(rentalRepo);
                    break;
                case 5:
                    initializeDatabase(rentalRepo);
                    break;
                case 6:
                    exitProgram(scanner);
                    return;
            }
        }
    }

    private static int getMenuChoice(Scanner scanner) {
        while (true) {
            System.out.print("Your choice: ");
            String input = scanner.nextLine().trim();

            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= 6) {
                    return choice;
                } else {
                    System.out.println("Please enter number 1-6!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a NUMBER (1-6)");
                System.out.println("Example: type '1' for Browse Bikes");
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n" + "━".repeat(45));
        System.out.println("MAIN MENU:");
        System.out.println("━".repeat(45));
        System.out.println("1. Browse Available Bikes");
        System.out.println("2. Rent a Bike");
        System.out.println("3. View Rental History");
        System.out.println("4. Database JOIN Demo");
        System.out.println("5. Initialize Database");
        System.out.println("6. Exit");
        System.out.println("━".repeat(45));
    }

    private static void displayAllBikes(BikeRepository bikeRepo) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("AVAILABLE BIKES");
        System.out.println("═".repeat(60));

        List<Bike> bikes = bikeRepo.findAllAvailable();

        if (bikes.isEmpty()) {
            System.out.println("No bikes available.");
            return;
        }

        System.out.printf("%-4s %-30s %-15s %-10s%n", "ID", "MODEL", "PRICE/HOUR", "STATUS");
        System.out.println("-".repeat(65));

        for (Bike bike : bikes) {
            String status = bike.isAvailable() ? "AVAILABLE" : "RENTED";

            System.out.printf("%-4d %-30s $%-14.2f %s%n",
                    bike.getId(),
                    bike.getModel(),
                    bike.getPricePerHour(),
                    status);
        }

        System.out.println("\nTOTAL: " + bikes.size() + " bikes available");
    }

    private static void rentBike(Scanner scanner, BikeRepository bikeRepo, RentalRepository rentalRepo) {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("RENT A BIKE");
        System.out.println("=".repeat(45));

        List<Bike> availableBikes = bikeRepo.findAllAvailable();
        if (availableBikes.isEmpty()) {
            System.out.println("No bikes available for rent.");
            return;
        }

        showAvailableBikesList(availableBikes);

        int bikeId = getIntInput(scanner, "Enter Bike ID: ");
        Bike selectedBike = bikeRepo.findById(bikeId);

        if (selectedBike == null) {
            System.out.println("Bike with ID " + bikeId + " not found!");
            return;
        }

        if (!selectedBike.isAvailable()) {
            System.out.println("This bike is already rented!");
            return;
        }

        System.out.println("\nCUSTOMER INFORMATION:");
        String firstName = getStringInput(scanner, "First Name: ");
        String lastName = getStringInput(scanner, "Last Name: ");
        String phone = getStringInput(scanner, "Phone Number: ");

        int hours = getIntInput(scanner, "Hours to rent (1-24): ");
        if (hours < 1 || hours > 24) {
            System.out.println("Hours must be between 1 and 24!");
            return;
        }

        double total = selectedBike.getPricePerHour() * hours;

        showRentalConfirmation(selectedBike, firstName, lastName, hours, total);

        if (!confirmAction(scanner, "Confirm rental? (yes/no): ")) {
            System.out.println("Rental cancelled.");
            return;
        }

        try {
            Integer userId = rentalRepo.findOrCreateUser(firstName, lastName, phone);

            if (userId == null) {
                System.out.println("Could not register customer!");
                return;
            }

            Rental rental = new Rental();
            rental.setBikeId(bikeId);
            rental.setUserId(userId);
            rental.setHours(hours);
            rental.setTotalPrice(total);
            rental.setStatus("ACTIVE");

            boolean saved = rentalRepo.save(rental);

            if (saved) {
                boolean updated = bikeRepo.updateStatus(bikeId, "RENTED");

                if (updated) {
                    System.out.println("\nRENTAL SUCCESSFUL!");
                    System.out.println("Rental saved to database");
                    System.out.println("Bike status updated to RENTED");
                } else {
                    System.out.println("Rental saved but bike status not updated!");
                }
            } else {
                System.out.println("Failed to save rental!");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showRentalHistoryMenu(Scanner scanner, RentalRepository rentalRepo) {
        while (true) {
            System.out.println("\n" + "📜".repeat(25));
            System.out.println("   RENTAL HISTORY");
            System.out.println("📜".repeat(25));
            System.out.println("1. View by Time Period");
            System.out.println("2. View by Bikes");
            System.out.println("3. View by Customer");
            System.out.println("4. View All Rentals");
            System.out.println("5. Back to Main Menu");
            System.out.println("-".repeat(45));

            System.out.print("Choose option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewHistoryByTime(scanner, rentalRepo);
                    break;
                case "2":
                    viewHistoryByBikes(scanner, rentalRepo);
                    break;
                case "3":
                    viewHistoryByCustomer(scanner, rentalRepo);
                    break;
                case "4":
                    displayAllRentals(rentalRepo);
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Please enter 1-5!");
            }
        }
    }

    private static void displayAllRentals(RentalRepository rentalRepo) {

    }

    private static void viewHistoryByTime(Scanner scanner, RentalRepository rentalRepo) {
        System.out.println("\nRENTALS BY TIME PERIOD");
        System.out.println("1. Today");
        System.out.println("2. This Week");
        System.out.println("3. This Month");
        System.out.println("4. This Year");
        System.out.println("5. All Time");
        System.out.println("-".repeat(45));

        System.out.print("Choose period: ");
        String period = scanner.nextLine().trim();

        List<Rental> rentals;

        switch (period) {
            case "1": rentals = rentalRepo.findRentalsByPeriod("TODAY"); break;
            case "2": rentals = rentalRepo.findRentalsByPeriod("WEEK"); break;
            case "3": rentals = rentalRepo.findRentalsByPeriod("MONTH"); break;
            case "4": rentals = rentalRepo.findRentalsByPeriod("YEAR"); break;
            case "5": rentals = rentalRepo.findAll(); break;
            default:
                System.out.println("Invalid choice!");
                return;
        }

        displayRentals(rentals, "Time Period");
    }

    private static void viewHistoryByBikes(Scanner scanner, RentalRepository rentalRepo) {
        System.out.println("\nRENTALS BY BIKES");

        System.out.print("Enter Bike ID (or 'all' for all bikes): ");
        String input = scanner.nextLine().trim();

        List<Rental> rentals;

        if (input.equalsIgnoreCase("all")) {
            rentals = rentalRepo.findAll();
        } else {
            try {
                int bikeId = Integer.parseInt(input);
                rentals = rentalRepo.findRentalsByBike(bikeId);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid Bike ID!");
                return;
            }
        }

        displayRentals(rentals, "Bike");
    }

    private static void viewHistoryByCustomer(Scanner scanner, RentalRepository rentalRepo) {
        System.out.println("\nFIND CUSTOMER'S RENTALS");

        System.out.println("Search by:");
        System.out.println("   [1] Customer Name");
        System.out.println("   [2] Phone Number");
        System.out.println("-".repeat(45));

        System.out.print("Choose (1 or 2): ");
        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            searchCustomerByName(scanner, rentalRepo);
        } else if (choice.equals("2")) {
            searchCustomerByPhone(scanner, rentalRepo);
        } else {
            System.out.println("Please type '1' or '2'!");
        }
    }

    private static void searchCustomerByName(Scanner scanner, RentalRepository rentalRepo) {
        System.out.println("\nENTER CUSTOMER NAME:");
        System.out.print("   First Name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("   Last Name: ");
        String lastName = scanner.nextLine().trim();

        if (firstName.isEmpty() && lastName.isEmpty()) {
            System.out.println("You must enter at least First OR Last name!");
            return;
        }

        System.out.println("\nSearching for customer: " +
                (firstName.isEmpty() ? "" : firstName) +
                (lastName.isEmpty() ? "" : " " + lastName));

        List<Rental> rentals = rentalRepo.findRentalsByCustomerName(firstName, lastName);

        if (rentals.isEmpty()) {
            System.out.println("No rentals found for this customer.");
        } else {
            System.out.println("Found " + rentals.size() + " rental(s)");
            displayRentals(rentals, "Customer: " + firstName + " " + lastName);
        }
    }

    private static void searchCustomerByPhone(Scanner scanner, RentalRepository rentalRepo) {
        System.out.println("\nENTER PHONE NUMBER:");
        System.out.print("   Phone: ");
        String phone = scanner.nextLine().trim();

        if (phone.isEmpty()) {
            System.out.println("Phone number cannot be empty!");
            return;
        }

        System.out.println("\nSearching by phone: " + phone);
        List<Rental> rentals = rentalRepo.findRentalsByCustomerPhone(phone);

        if (rentals.isEmpty()) {
            System.out.println("No rentals found for this phone number.");
        } else {
            System.out.println("Found " + rentals.size() + " rental(s)");
            displayRentals(rentals, "Phone: " + phone);
        }
    }

    private static void showJoinDemo(Scanner scanner, RentalRepository rentalRepo) {
        System.out.println("\nDATABASE JOIN DEMONSTRATION");

        System.out.print("Enter Rental ID: ");
        String input = scanner.nextLine().trim();

        try {
            int rentalId = Integer.parseInt(input);
            rentalRepo.showRentalDetails(rentalId);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid Rental ID!");
        }
    }

    public static void initializeDatabase(RentalRepository rentalRepo) {
        System.out.println("\nINITIALIZING DATABASE...");
        rentalRepo.createTablesIfNotExist();
        System.out.println("Database ready!");
    }

    private static void exitProgram(Scanner scanner) {
        System.out.println("\nThank you for visiting!");
        System.out.println("Come back soon!");
        scanner.close();
    }

    private static void showAvailableBikesList(List<Bike> bikes) {
        System.out.println("\nAVAILABLE BIKES:");
        System.out.printf("%-4s %-25s %-15s%n", "ID", "MODEL", "PRICE/HOUR");
        System.out.println("-".repeat(45));

        for (Bike bike : bikes) {
            System.out.printf("%-4d %-25s $%-14.2f%n",
                    bike.getId(),
                    bike.getModel(),
                    bike.getPricePerHour());
        }
    }

    private static void showRentalConfirmation(Bike bike, String firstName, String lastName, int hours, double total) {
        System.out.println("\n" + "✓".repeat(45));
        System.out.println("RENTAL CONFIRMATION");
        System.out.println("✓".repeat(45));
        System.out.println("Bike: " + bike.getModel());
        System.out.println("Customer: " + firstName + " " + lastName);
        System.out.println("Hours: " + hours);
        System.out.println("Price/hour: $" + String.format("%.2f", bike.getPricePerHour()));
        System.out.println("Total: $" + String.format("%.2f", total));
        System.out.println("✓".repeat(45));
    }

    private static int getIntInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }

    private static String getStringInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("This field cannot be empty!");
        }
    }

    private static boolean confirmAction(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("yes") || input.equals("y")) {
                return true;
            } else if (input.equals("no") || input.equals("n")) {
                return false;
            }
            System.out.println("Please answer 'yes' or 'no'");
        }
    }

    private static void displayRentals(List<Rental> rentals, String title) {
        System.out.println("\n" + "=".repeat(90));
        System.out.println("   " + title.toUpperCase());
        System.out.println("=".repeat(90));

        if (rentals == null || rentals.isEmpty()) {
            System.out.println("No rentals found.");
            return;
        }

        System.out.printf("%-8s %-20s %-20s %-8s %-10s %-12s%n",
                "ID", "BIKE", "CUSTOMER", "HOURS", "TOTAL", "DATE");
        System.out.println("-".repeat(90));

        double totalRevenue = 0;
        for (Rental rental : rentals) {
            String bikeName = rental.getBikeModel() != null ?
                    rental.getBikeModel() : "Bike #" + rental.getBikeId();

            String customerName = rental.getUserName() != null ?
                    rental.getUserName() : "Customer #" + rental.getUserId();

            if (bikeName.length() > 18) bikeName = bikeName.substring(0, 15) + "...";
            if (customerName.length() > 18) customerName = customerName.substring(0, 15) + "...";

            System.out.printf("%-8d %-20s %-20s %-8d $%-9.2f %-12s%n",
                    rental.getId(),
                    bikeName,
                    customerName,
                    rental.getHours(),
                    rental.getTotalPrice(),
                    formatDate(rental.getRentalDate()));

            totalRevenue += rental.getTotalPrice();
        }

        System.out.println("=".repeat(90));
        System.out.printf("%-58s TOTAL: $%.2f%n", "", totalRevenue);
        System.out.printf("%-58s COUNT: %d rentals%n", "", rentals.size());
    }

    private static String formatDate(java.time.LocalDateTime date) {
        if (date == null) return "N/A";
        return date.toLocalDate().toString();
    }
}