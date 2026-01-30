package com.bikerental.models;

public class Bike {

    private int id;
    private String model;
    private String type;
    private double pricePerHour;
    private boolean available;

    // Конструктор по умолчанию
    public Bike() {
    }

    // Основной конструктор
    public Bike(int id, String model, String type, double pricePerHour, boolean available) {
        this.id = id;
        this.model = model;
        this.type = type;
        this.pricePerHour = pricePerHour;
        this.available = available;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Bike{" +
                "id=" + id +
                ", model='" + model + '\'' +
                ", type='" + type + '\'' +
                ", pricePerHour=" + pricePerHour +
                ", available=" + available +
                '}';
    }
}
