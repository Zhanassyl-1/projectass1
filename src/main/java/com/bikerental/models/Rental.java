package com.bikerental.models;

public class Rental {
    private Integer id;
    private Integer bikeId;
    private Integer userId;
    private Integer hours;
    private Double totalPrice;
    private String status;
    private String bikeModel;

    // Конструкторы
    public Rental() {}

    public Rental(Integer id, Integer bikeId, Integer userId, Integer hours,
                  Double totalPrice, String status, String bikeModel) {
        this.id = id;
        this.bikeId = bikeId;
        this.userId = userId;
        this.hours = hours;
        this.totalPrice = totalPrice;
        this.status = status;
        this.bikeModel = bikeModel;
    }

    // Геттеры
    public Integer getId() { return id; }
    public Integer getBikeId() { return bikeId; }
    public Integer getUserId() { return userId; }
    public Integer getHours() { return hours; }
    public Double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public String getBikeModel() { return bikeModel; }

    // Сеттеры
    public void setId(Integer id) { this.id = id; }
    public void setBikeId(Integer bikeId) { this.bikeId = bikeId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setHours(Integer hours) { this.hours = hours; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    public void setStatus(String status) { this.status = status; }
    public void setBikeModel(String bikeModel) { this.bikeModel = bikeModel; }
}