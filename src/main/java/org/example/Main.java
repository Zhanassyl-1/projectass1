package org.example;

import com.bikerental.repositories.RentalRepository;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== BIKE RENTAL SYSTEM ===");

        RentalRepository repo = new RentalRepository();

        // Тест JOIN операции
        System.out.println("\nTesting JOIN operation...");
        repo.showRentalDetails(1);

        // Тест аренды
        System.out.println("\nTesting bike rental...");
        repo.rentBike(1, 1, 3);
    }
}