package com.bikerental.security;

import com.bikerental.models.Role;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class AccessManager {
    private static AccessManager instance;
    private Role currentRole = Role.VIEWER;

    private AccessManager() {
    }

    public static synchronized AccessManager getInstance() {
        if (instance == null) {
            instance = new AccessManager();
        }
        return instance;
    }

    public void login(Scanner scanner) {
        System.out.println("\nLOGIN ROLE (simulated access control):");
        System.out.println("1. ADMIN");
        System.out.println("2. MANAGER");
        System.out.println("3. EDITOR");
        System.out.println("4. VIEWER");
        System.out.print("Choose role: ");
        String input = scanner.nextLine().trim();

        switch (input) {
            case "1":
                currentRole = Role.ADMIN;
                break;
            case "2":
                currentRole = Role.MANAGER;
                break;
            case "3":
                currentRole = Role.EDITOR;
                break;
            default:
                currentRole = Role.VIEWER;
        }

        System.out.println("Active role: " + currentRole.name().toUpperCase(Locale.ROOT));
    }

    public Role getCurrentRole() {
        return currentRole;
    }

    public boolean hasAny(Role... roles) {
        return Arrays.stream(roles).anyMatch(role -> role == currentRole);
    }

    public boolean requireAny(Role... roles) {
        if (hasAny(roles)) {
            return true;
        }
        System.out.println("Access denied for role: " + currentRole);
        return false;
    }
}
