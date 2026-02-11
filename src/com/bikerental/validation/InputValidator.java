package com.bikerental.validation;

import java.util.List;
import java.util.function.Predicate;

public class InputValidator {
    private static final String NAME_REGEX = "^[A-Za-z][A-Za-z\\s'-]{1,29}$";
    private static final String PHONE_REGEX = "^[0-9]{7,15}$";

    public static boolean validateAll(String value, List<Predicate<String>> rules) {
        return rules.stream().allMatch(rule -> rule.test(value));
    }

    public static Predicate<String> isNotBlank() {
        return value -> value != null && !value.trim().isEmpty();
    }

    public static Predicate<String> isValidName() {
        return value -> value != null && value.matches(NAME_REGEX);
    }

    public static Predicate<String> isValidPhone() {
        return value -> value != null && value.matches(PHONE_REGEX);
    }

    public static Predicate<String> isValidHours() {
        return value -> {
            try {
                int hours = Integer.parseInt(value);
                return hours >= 1 && hours <= 24;
            } catch (NumberFormatException e) {
                return false;
            }
        };
    }
}
