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

    // Конструктор по умолчанию
    public Rental() {
    }

    // Основной конструктор
    public Rental(Integer id,
                  Integer bikeId,
                  Integer userId,
                  Integer hours,
                  Double totalPrice,
                  String status,
                  LocalDateTime rentalDate,
                  String bikeModel,
                  String userName) {
        this.id = id;
        this.bikeId = bikeId;
        this.userId = userId;
        this.hours = hours;
        this.totalPrice = totalPrice;
        this.status = status;
        this.rentalDate = rentalDate;
        this.bikeModel = bikeModel;
        this.userName = userName;
    }

    public Integer getId() {
        return id;
    }

    public Integer getBikeId() {
        return bikeId;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getHours() {
        return hours;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getRentalDate() {
        return rentalDate;
    }

    public String getBikeModel() {
        return bikeModel;
    }

    public String getUserName() {
        return userName;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setBikeId(Integer bikeId) {
        this.bikeId = bikeId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRentalDate(LocalDateTime rentalDate) {
        this.rentalDate = rentalDate;
    }

    public void setBikeModel(String bikeModel) {
        this.bikeModel = bikeModel;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "Rental{" +
                "id=" + id +
                ", bikeId=" + bikeId +
                ", userId=" + userId +
                ", hours=" + hours +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                ", rentalDate=" + rentalDate +
                ", bikeModel='" + bikeModel + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
