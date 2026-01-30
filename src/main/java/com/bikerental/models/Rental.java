package com.bikerental.models;

import java.time.LocalDateTime;

public class Rental {
    private Integer id;
    private Integer bikeId;
    private Integer userId;
    private Integer hours;
    private Double totalPrice;
    private String status;
    private LocalDateTime rentalDate;
    private String bikeModel;
    private String userName;


    public Rental() {}


    public Integer getId() { return id; }
    public Integer getBikeId() { return bikeId; }
    public Integer getUserId() { return userId; }
    public Integer getHours() { return hours; }
    public Double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public LocalDateTime getRentalDate() { return rentalDate; }
    public String getBikeModel() { return bikeModel; }
    public String getUserName() { return userName; }


    public void setId(Integer id) { this.id = id; }
    public void setBikeId(Integer bikeId) { this.bikeId = bikeId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setHours(Integer hours) { this.hours = hours; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    public void setStatus(String status) { this.status = status; }
    public void setRentalDate(LocalDateTime rentalDate) { this.rentalDate = rentalDate; }
    public void setBikeModel(String bikeModel) { this.bikeModel = bikeModel; }
    public void setUserName(String userName) { this.userName = userName; }
}