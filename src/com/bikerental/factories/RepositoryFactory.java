package com.bikerental.factories;

import com.bikerental.repositories.BikeRepository;
import com.bikerental.repositories.CategoryRepository;
import com.bikerental.repositories.RentalRepository;

public class RepositoryFactory {
    public static <T> T create(Class<T> type) {
        if (type == BikeRepository.class) {
            return type.cast(new BikeRepository());
        }
        if (type == RentalRepository.class) {
            return type.cast(new RentalRepository());
        }
        if (type == CategoryRepository.class) {
            return type.cast(new CategoryRepository());
        }
        throw new IllegalArgumentException("Unsupported repository type: " + type.getName());
    }
}
