package com.bikerental.models;

public class Bike {
    private int id;
    private String model;
    private String type;
    private double pricePerHour;
    private boolean isAvailable;
    private Integer categoryId;
    private String categoryName;


    public Bike() {}

    public Bike(int id, String model, String type, double pricePerHour, boolean isAvailable) {
        this.id = id;
        this.model = model;
        this.type = type;
        this.pricePerHour = pricePerHour;
        this.isAvailable = isAvailable;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(double pricePerHour) { this.pricePerHour = pricePerHour; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}
